package com.sf.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.common.Constants;
import com.sf.common.StringConst;
import com.sf.entity.ActiveUser;
import com.sf.entity.User;
import com.sf.entity.dto.UserDTO;
import com.sf.enums.SexEnum;
import com.sf.enums.UserStateEnum;
import com.sf.exception.ServiceException;
import com.sf.mapper.ActiveUserMapper;
import com.sf.mapper.UserMapper;
import com.sf.service.IUserService;
import com.sf.utils.HttpUtil;
import com.sf.utils.ObjectActionUtils;
import com.sf.utils.RedisUtils;
import com.sf.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMailMessage;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leung
 * @since 2022-05-30
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JavaMailSender javaMailSender; // 邮件发送器

    @Resource
    private ActiveUserMapper activeUserMapper;


    @Value("${spring.mail.username}")
    private String from; // 邮件发送者

    @Override
    public void userDisabled(Integer userID) {// 用户状态禁用
        User user = userMapper.selectById(userID);
        if (user.getDisabled() == UserStateEnum.ENABLE) {
            // 1.修改数据库用户状态
            user.setDisabled(UserStateEnum.DISABLED);// 将用户状态改为禁用
            userMapper.updateById(user);// 将更新同步到数据库
            // 2.获取用户token并删除该用户ID与token关系缓存(如果用户正在登录状态)
            Map<Object, Object> userIDTokenMap = RedisUtils.mapFromRedis(StringConst.USERID_TOKEN_REDIS_KEY);
            String token = (String) userIDTokenMap.get(userID.toString());
            userIDTokenMap.remove(userID.toString());
            // 必须将旧的map删除重建，否则被删掉的键值对无法同步删除
            stringRedisTemplate.delete(StringConst.USERID_TOKEN_REDIS_KEY);
            RedisUtils.mapToRedis(StringConst.USERID_TOKEN_REDIS_KEY, userIDTokenMap, Constants.USERID_TOKEN_REDIS_TIMEOUT);
            stringRedisTemplate.persist(StringConst.USERID_TOKEN_REDIS_KEY);
            //3.根据token删除用户信息缓存
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(token))) {
                stringRedisTemplate.delete(token);
            }
        } else {
            throw new ServiceException(Constants.CODE_400, "该用户已经是禁用状态！");
        }
    }

    @Override
    public void userEnable(Integer userID) {// 解除用户禁用
        User user = userMapper.selectById(userID);
        if (user.getDisabled() == UserStateEnum.DISABLED) {
            user.setDisabled(UserStateEnum.ENABLE);// 将用户状态改为启用
            userMapper.updateById(user);// 将更新同步到数据库
        } else {
            throw new ServiceException(Constants.CODE_400, "该用户已经是启用状态！");
        }
    }

    @Override
    public void userSignOut(String token) {// 登出(删除redis用户信息及token关系缓存)
        if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(token))) {
            // 1.删除该用户ID与token关系缓存
            Map<Object, Object> userIDTokenMap = RedisUtils.mapFromRedis(StringConst.USERID_TOKEN_REDIS_KEY);
            userIDTokenMap.remove(RedisUtils.getCurrentUserId(token).toString());
            stringRedisTemplate.delete(StringConst.USERID_TOKEN_REDIS_KEY);
            RedisUtils.mapToRedis(StringConst.USERID_TOKEN_REDIS_KEY, userIDTokenMap, Constants.USERID_TOKEN_REDIS_TIMEOUT);
            stringRedisTemplate.persist(StringConst.USERID_TOKEN_REDIS_KEY);
            // 2.删除用户信息缓存
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(token))) {
                stringRedisTemplate.delete(token);
            }
        } else {
            throw new ServiceException(Constants.CODE_400, "用户并未登录！");
        }
    }

    @Override
    public void userLogout(UserDTO userDTO) {// 注销(删除redis用户缓存信息)
        // 通过token查询redis中的用户信息
        JSONObject userFromRedis = RedisUtils.getUserRedis(userDTO.getToken());
        setUserRedis(userDTO, userFromRedis);
        if (StrUtil.isNotBlank(userDTO.getCode())) {
            String email = userDTO.getEmail(); // 从redis缓存中获取email
            if (checkEmailCode(email, userDTO.getCode())) { // 验证邮箱与验证码信息
                // 1.检测有人脸信息则删除人脸库人脸信息
                if (!Objects.equals(userDTO.getUserFace(), "null") && faceDelete(userDTO.getId().toString(), userDTO.getUserFace()) == null) {//判断是否存在人脸信息
                    // 有人脸且删除失败则抛出异常
                    throw new ServiceException(Constants.CODE_600, "人脸删除失败");
                }
                // 2.删除用户缓存
                userSignOut(userDTO.getToken());
                // 3.从数据库删除用户信息
                userMapper.deleteById(userDTO.getId());
                // 4.操作成功，删除redis缓存中对应的验证码信息
                stringRedisTemplate.delete(StringConst.CODE_EMAIL + email);
            } else {
                throw new ServiceException(Constants.CODE_600, "验证码不匹配");
            }
        }
    }

    @Override
    public UserDTO userLogin(String username, String password, String userFace) { // 用户登录
        if (StrUtil.isNotBlank(username) && StrUtil.isNotBlank(password) && StrUtil.isBlank(userFace)) {
            return userPwdLogin(username, password);
        } else if (StrUtil.isBlank(username) && StrUtil.isBlank(password) && StrUtil.isNotBlank(userFace)) {
            return userFaceLogin(userFace);
        } else {
            throw new ServiceException(Constants.CODE_400, "参数异常");
        }
    }

    public UserDTO userPwdLogin(String username, String password) {
        User user;
        if (checkEmail(username)) { // 用户名格式确定登录方式
            user = checkUser(StringConst.EMAIL_LOGIN, "email", username); // 邮箱登录方式
        } else {
            user = checkUser(StringConst.USERNAME_LOGIN, "username", username); // 用户名登录方式
        }
        if (user != null && password.equals(user.getPassword())) { // 用户存在且密码一致
            Map<Object, Object> userIDTokenMap = RedisUtils.mapFromRedis(StringConst.USERID_TOKEN_REDIS_KEY);
            if (user.getDisabled() == UserStateEnum.DISABLED) { // 判断用户是否被禁用
                throw new ServiceException(Constants.CODE_400, "该账号已经被禁用！");
            }
            if (userIDTokenMap.containsKey(user.getId().toString())) { // 判断是否已登录
                throw new ServiceException(Constants.CODE_400, "请不要重复登录！");
            }
            //将用户登录信息存入活跃用户表
            ActiveUser activeUser = new ActiveUser();
            activeUser.setTime(DateUtil.now());
            activeUser.setUserId(user.getId());
            activeUserMapper.insert(activeUser);

            UserDTO loginUser = new UserDTO();
            // 通过浅拷贝设置用户信息,忽略空值
            BeanUtil.copyProperties(user, loginUser, CopyOptions.create().setIgnoreNullValue(true).setIgnoreCase(true));

            return setToken(loginUser); // 返回设置token后的用户对象
        } else { // 用户提交信息不匹配
            throw new ServiceException(Constants.CODE_400, "账号或密码错误");
        }
    }

    public UserDTO userFaceLogin(String userFace) {
        User user = new User();
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/search";
        // 设置请求体json格式参数
        JSONObject jsonUpload = new JSONObject();
        jsonUpload.set("image", userFace);
        jsonUpload.set("group_id_list", "speakfreely");
        jsonUpload.set("image_type", "BASE64");

        // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
        String accessToken = StringConst.BAIDU_ACCESS_TOKEN;

        String result = null;
        try {// 发送人脸搜索请求
            result = HttpUtil.post(url, accessToken, "application/json", jsonUpload.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonResult = new JSONObject(result); // 将匹配结果转为json对象
        if (jsonResult.get("error_msg").equals("SUCCESS")) { // 检测匹配结果是否通过
            jsonResult = new JSONObject(jsonResult.get("result")); // 获取检测结果中的result信息
            // 获取匹配到的用户信息列表
            List<JSONObject> userResult = (List<JSONObject>) jsonResult.get("user_list");
            // 检测吻合度大于85则表示验证通过
            if (Float.parseFloat(String.valueOf(userResult.get(0).get("score"))) > 85.0) {
                // 从人脸匹配结果中获取用户id信息，以此从系统mysql数据库获取对应用户信息
                QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
                userQueryWrapper.eq("id", userResult.get(0).get("user_id"));
                user = userMapper.selectOne(userQueryWrapper);

                Map<Object, Object> userIDTokenMap = RedisUtils.mapFromRedis(StringConst.USERID_TOKEN_REDIS_KEY);
                if (user.getDisabled() == UserStateEnum.DISABLED) { // 判断用户是否被禁用
                    throw new ServiceException(Constants.CODE_400, "该账号已经被禁用！");
                }
                if (userIDTokenMap.containsKey(user.getId().toString())) { // 判断是否已登录
                    throw new ServiceException(Constants.CODE_400, "请不要重复登录！");
                }

                //将用户登录信息存入活跃用户表
                ActiveUser activeUser = new ActiveUser();
                activeUser.setTime(DateUtil.now());
                activeUser.setUserId(user.getId());
                activeUserMapper.insert(activeUser);

                UserDTO loginUser = new UserDTO();
                // 通过浅拷贝设置用户信息,忽略空值
                BeanUtil.copyProperties(user, loginUser, CopyOptions.create().setIgnoreNullValue(true).setIgnoreCase(true));

                return setToken(loginUser); // 返回设置token后的用户对象
            } else {
                throw new ServiceException(Constants.CODE_400, "没有匹配的用户信息");
            }
        } else {
            throw new ServiceException(Constants.CODE_400, "人脸验证失败");
        }
    }

    @Override
    public UserDTO userRegister(UserDTO userDTO) { // 用户注册
        String email = userDTO.getEmail();
        if (checkEmailCode(email, userDTO.getCode())) { // 验证邮箱与验证码信息
            if (checkUser(StringConst.USER_REGISTER, "username", userDTO.getUsername()) != null) {
                throw new ServiceException(Constants.CODE_600, "用户名已被注册");
            } else if (checkUser(StringConst.USER_REGISTER, "email", email) != null) {
                throw new ServiceException(Constants.CODE_600, "该邮箱已被绑定");
            }
            User user = new User(); // 实例化一个新用户对象
            ObjectActionUtils.copyByName(userDTO, user);// 通过浅拷贝更新用户信息,忽略空值
            userMapper.insert(user); // 向数据库插入新用户信息
            // 修改成功，删除redis缓存中对应的验证码信息
            stringRedisTemplate.delete(StringConst.CODE_EMAIL + email);
            // 直接使用新注册的用户信息登录
            return userPwdLogin(userDTO.getUsername(), userDTO.getPassword());// 设置token并返回
        } else {
            throw new ServiceException(Constants.CODE_600, "验证码不匹配");
        }
    }

    @Override
    public UserDTO userInfoModify(UserDTO userDTO) { // 用户信息修改操作
        // 通过token查询redis中的用户信息
        JSONObject userFromRedis = RedisUtils.getUserRedis(userDTO.getToken());
        if (StrUtil.isNotBlank(userDTO.getCode())) {
            String email = (String) userFromRedis.get("email"); // 从redis缓存中获取email
            if (checkEmailCode(email, userDTO.getCode())) { // 验证邮箱与验证码信息
                infoModify(userDTO, userFromRedis.get("id").toString()); // 调用更新方法更新用户信息
                // 修改成功，删除redis缓存中对应的验证码信息
                stringRedisTemplate.delete(StringConst.CODE_EMAIL + email);
                return setToken(userDTO);// 更新token并返回
            } else {
                throw new ServiceException(Constants.CODE_600, "验证码不匹配");
            }
        } else if (isBaseInfo(userDTO)) {
            infoModify(userDTO, userFromRedis.get("id").toString()); // 调用更新方法更新用户信息
            return setToken(userDTO);// 更新token并返回
        } else {
            throw new ServiceException(Constants.CODE_600, "重要信息修改需要邮箱验证");
        }
    }

    /*判断是否含有用户重要信息*/
    public Boolean isBaseInfo(UserDTO userDTO) {
        return userDTO.getUsername() == null && userDTO.getEmail() == null && userDTO.getId() == null &&
                userDTO.getPassword() == null && userDTO.getCode() == null && userDTO.getRole() == null;
    }

    /*进行用户信息修改实际操作*/
    public void infoModify(UserDTO userDTO, String userId) {
        if (userDTO.getId() != null) {
            throw new ServiceException(Constants.CODE_600, "用户ID一经注册不能被修改");
        } else if (userDTO.getUsername() != null) {
            throw new ServiceException(Constants.CODE_600, "用户名一经注册不支持修改");
        } else if (userDTO.getEmail() != null) {
            throw new ServiceException(Constants.CODE_600, "请不要在此处修改邮箱");
        } else {
            User user = new User();
            ObjectActionUtils.copyByName(userDTO, user);// 通过浅拷贝更新用户信息,忽略空值
            user.setId(Integer.valueOf(userId)); // 设置id
            userMapper.updateById(user); // 将更新同步到数据库
        }
    }

    @Override
    public UserDTO emailModify(UserDTO userDTO) {
        String email = userDTO.getEmail(); // 获取新email
        if (checkEmailCode(email, userDTO.getCode())) { // 验证新邮箱与验证码信息
            // 检验新邮箱是否被绑定
            if (checkUser(StringConst.MODIFY_EMAIL, "email", email) != null) {
                throw new ServiceException(Constants.CODE_600, "该邮箱已被绑定");
            }
            User user = new User();
            user.setEmail(email); // 设置用户邮箱为新邮箱
            user.setId(RedisUtils.getCurrentUserId(userDTO.getToken())); // 设置id
            userMapper.updateById(user); // 将更新同步到数据库
            // 修改成功，删除redis缓存中对应的验证码信息
            stringRedisTemplate.delete(StringConst.CODE_EMAIL + email);

            return setToken(userDTO);// 更新token并返回
        } else {
            throw new ServiceException(Constants.CODE_600, "验证码不匹配");
        }
    }

    @Override
    public JSONObject faceUpload(UserDTO userDTO) { // 人脸录入
        Integer user_id = RedisUtils.getCurrentUserId(userDTO.getToken());// 获取当前用户ID
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/update";
        // 构建人脸上传请求体json格式数据
        JSONObject jsonUpload = new JSONObject();
        jsonUpload.set("image", userDTO.getUserFace());
        jsonUpload.set("group_id", "speakfreely");
        jsonUpload.set("user_id", user_id);
        jsonUpload.set("image_type", "BASE64");
        jsonUpload.set("quality_control", "LOW");
        jsonUpload.set("action_type", "REPLACE");


        // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
        String accessToken = StringConst.BAIDU_ACCESS_TOKEN;

        String result = null;
        try { // 发送人脸更新请求
            result = HttpUtil.post(url, accessToken, "application/json", jsonUpload.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //log.info(result);
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.get("error_msg").equals("SUCCESS")) { // 检测上传结果
            jsonResult = new JSONObject(jsonResult.get("result"));
            User user = new User();
            // 设置用户人脸信息为人脸库中的人脸token值
            user.setUserFace(jsonResult.get("face_token").toString());
            user.setId(RedisUtils.getCurrentUserId(userDTO.getToken())); // 设置用户id
            userMapper.updateById(user); // 将人脸token信息更新同步到数据库
            userDTO.setUserFace(jsonResult.get("face_token").toString());
            JSONObject userFromRedis = RedisUtils.getUserRedis(userDTO.getToken());
            setUserRedis(userDTO, userFromRedis); // 用户redis缓存同步
            //将用户缓存存入redis
            RedisUtils.objToRedis(userDTO.getToken(), userFromRedis, Constants.USER_REDIS_TIMEOUT);
        }  // 上传失败直接将失败结果返回
        return jsonResult;
    }

    /*从人脸库删除人脸操作*/
    public JSONObject faceDelete(String user_id, String faceToken) {
        // 请求url
        String url = "https://aip.baidubce.com/rest/2.0/face/v3/faceset/face/delete";
        // 设置请求体json格式参数
        JSONObject jsonDelete = new JSONObject();
        jsonDelete.set("face_token", faceToken);
        jsonDelete.set("group_id", "speakfreely");
        jsonDelete.set("user_id", user_id);

        // 注意这里仅为了简化编码每一次请求都去获取access_token，线上环境access_token有过期时间， 客户端可自行缓存，过期后重新获取。
        String accessToken = StringConst.BAIDU_ACCESS_TOKEN;

        String result = null;
        try {// 发送人脸删除请求
            result = HttpUtil.post(url, accessToken, "application/json", jsonDelete.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonResult = new JSONObject(result);
        if (jsonResult.get("error_code").equals(0)) {// 检验是否删除成功
            return jsonResult;
        } else {
            return null;// 删除失败返回null
        }
    }

    @Override
    public void userPwdReset(String email, String newPwd, String code) {
        if (checkEmailCode(email, code)) { // 验证邮箱与验证码信息
            User user = checkUser(StringConst.PWD_RESET, "email", email); // 密码重置操作
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>(); // 包装更新器
            updateWrapper.eq("id", user.getId()); // 根据检查结果用户的id确定更新对象(唯一性)
            updateWrapper.set("password", newPwd); // 只更新password字段
            userMapper.update(null, updateWrapper); // 将更新同步到数据库

            // 修改成功，删除redis缓存中对应的验证码信息
            stringRedisTemplate.delete(StringConst.CODE_EMAIL + email);
        } else {
            throw new ServiceException(Constants.CODE_600, "验证码不匹配");
        }
    }

    /*邮件发送方法*/
    @Override
    public void sendEmailCode(String action, String email) {
        if (!checkEmail(email)) { // 先检验邮箱格式
            throw new ServiceException(Constants.CODE_400, "邮箱格式错误"); // 格式不正确时抛出异常
        }
        String codeFromRedis = stringRedisTemplate.opsForValue().get(StringConst.CODE_EMAIL + email);
        if ("".equals(codeFromRedis) || codeFromRedis == null) { // 检验redis有没有对应的验证码
            String title = getEmailTitle(action, email); // 先获取对应邮件标题
            String code = RandomUtil.randomString(4); // 再获取验证码
            String content = "您本次操作的验证码为: " + code + "，两分钟内有效！"; // 设置邮件内容(复杂时可考虑封装)
            MimeMessage mailMessage = javaMailSender.createMimeMessage();
            try {
                MimeMessageHelper messageHelper = new MimeMessageHelper(mailMessage, true, "UTF-8");
                messageHelper.setFrom(from); // 设置发件人Email
                messageHelper.setSubject(title); // 设置邮件主题
                messageHelper.setText(content); // 设置邮件主题内容
                messageHelper.setTo(email); // 设定收件人Email
            } catch (MessagingException e) {
                e.printStackTrace();
            }
            javaMailSender.send(mailMessage); // 发送邮件
            //将code放入redis 120s过期
            stringRedisTemplate.opsForValue().set(StringConst.CODE_EMAIL + email, code, 120, TimeUnit.SECONDS);
        } else {
            throw new ServiceException(Constants.CODE_600, "验证码有效期内请勿重复获取");
        }
    }

    /*根据操作标识及邮箱返回邮件标题*/
    public String getEmailTitle(String action, String email) {
        if (checkUser(StringConst.SEND_EMAIL, "email", email) == null) { // 用户为空才可创建邮件
            if (action.equals(StringConst.EMAIL_REGISTER)) { // 注册邮件操作
                return "用户注册验证码";
            } else if (action.equals(StringConst.EMAIL_MODIFY)) { // 换绑邮件操作
                return "邮箱换绑验证码";
            } else { // 用户不存在时可以进行的操作均未执行，抛出异常
                throw new ServiceException(Constants.CODE_400, "未知操作");
            }
        } else { // 其他操作需用户存在
            switch (action) {
                case StringConst.EMAIL_PWD_RESET:  // 重置密码邮件
                    return "密码重置验证码";
                case StringConst.EMAIL_INFO_MODIFY:  // 信息修改邮件
                    return "用户信息修改验证码";
                case StringConst.EMAIL_LOGOUT:  // 用户注销邮件
                    return "用户注销验证码";
                default:  // 用户存在时可以进行的操作均未执行则抛出异常
                    throw new ServiceException(Constants.CODE_600, "该邮箱已被绑定");
            }
        }
    }

    /*根据传入参数动态创建一个邮件实例返回*/
    public SimpleMailMessage createMail(String email, String title, String content) {
        SimpleMailMessage mailMessage = new SimpleMailMessage(); // 实例化一个空邮件对象
        mailMessage.setFrom(from); // 定义邮件发送方
        mailMessage.setTo(email); // 定义邮件接收方
        mailMessage.setSubject(title); // 定义邮件主题
        mailMessage.setText(content); // 定义邮件内容
        return mailMessage;
    }

    /*为用户设置token*/
    public UserDTO setToken(UserDTO userDTO) {
        String token = userDTO.getToken(); // 获取token(未登录用户无token信息)
        if (token == null || token.equals("")) { // 检测用户对象是否存在token
            token = TokenUtils.getToken(userDTO.getId().toString());
            userDTO.setToken(token); // 不存在则设置用户token
            //将用户缓存存入redis
            RedisUtils.objToRedis(token, userDTO, Constants.USER_REDIS_TIMEOUT);
            // 将用户id与token对应关系以map存入redis
            Map<Object, Object> userIDTokenMap = new HashMap<>();
            if (Boolean.TRUE.equals(stringRedisTemplate.hasKey(StringConst.USERID_TOKEN_REDIS_KEY))) {
                userIDTokenMap = RedisUtils.mapFromRedis(StringConst.USERID_TOKEN_REDIS_KEY);
            }
            userIDTokenMap.put(userDTO.getId().toString(), userDTO.getToken());
            // 将系统用户ID与token的Map存入redis并设置永不过期
            RedisUtils.mapToRedis(StringConst.USERID_TOKEN_REDIS_KEY, userIDTokenMap, Constants.USERID_TOKEN_REDIS_TIMEOUT);
            stringRedisTemplate.persist(StringConst.USERID_TOKEN_REDIS_KEY);
        } else {//根据token获取redis中的用户信息
            JSONObject userFromRedis = RedisUtils.getUserRedis(token);
            setUserRedis(userDTO, userFromRedis); // 用户redis缓存同步
            //将用户缓存存入redis
            RedisUtils.objToRedis(token, userFromRedis, Constants.USER_REDIS_TIMEOUT);
        }
        return userDTO;
    }

    /*将用户信息中的id、username、email与用户的redis缓存同步*/
    public void setUserRedis(UserDTO userDTO, JSONObject userFromRedis) {
        // 将用户信息拷贝到用户redis对象
        if (userDTO.getId() == null) { // id只能取不能更新
            userDTO.setId(Integer.valueOf(userFromRedis.get("id").toString()));
        }
        if (StrUtil.isBlank(userDTO.getUsername())) { // username只能取不能更新
            userDTO.setUsername((String) userFromRedis.get("username"));
        }
        if (StrUtil.isBlank(userDTO.getEmail())) { // 取邮箱信息
            userDTO.setEmail(String.valueOf(userFromRedis.get("email")));
        } else { // 邮箱可以被更新(邮箱换绑)，因此其他情况下的信息修改不应当涉及到邮箱信息(后台须加验证)
            userFromRedis.set("email", userDTO.getEmail());
        }
        if (StrUtil.isBlank(userDTO.getPassword())) { // 取密码信息
            userDTO.setPassword(String.valueOf(userFromRedis.get("password")));
        } else {
            userFromRedis.set("password", userDTO.getPassword());
        }
        if (StrUtil.isBlank(userDTO.getNickname())) { // 取昵称信息
            userDTO.setNickname(String.valueOf(userFromRedis.get("nickname")));
        } else {
            userFromRedis.set("nickname", userDTO.getNickname());
        }
        if (StrUtil.isBlank(userDTO.getPhone())) { // 取手机号信息
            userDTO.setPhone(String.valueOf(userFromRedis.get("phone")));
        } else {
            userFromRedis.set("phone", userDTO.getPhone());
        }
        if (userDTO.getSex() == null) { // 取性别信息
            userDTO.setSex(userFromRedis.get("sex").equals(SexEnum.MAN) ? SexEnum.MAN : SexEnum.WOMAN);
        } else {
            userFromRedis.set("sex", userDTO.getSex());
        }
        if (StrUtil.isBlank(userDTO.getAddress())) { // 取地址信息
            userDTO.setAddress(String.valueOf(userFromRedis.get("address")));
        } else {
            userFromRedis.set("address", userDTO.getAddress());
        }
        if (StrUtil.isBlank(userDTO.getAvatarUrl())) { // 取头像信息
            userDTO.setAvatarUrl(String.valueOf(userFromRedis.get("avatarUrl")));
        } else {
            userFromRedis.set("avatarUrl", userDTO.getAvatarUrl());
        }
        if (StrUtil.isBlank(userDTO.getRole())) { // 取角色信息
            userDTO.setRole(String.valueOf(userFromRedis.get("role")));
        } else {
            userFromRedis.set("role", userDTO.getRole());
        }
        if (StrUtil.isBlank(userDTO.getToken())) { // 取token信息
            userDTO.setToken(String.valueOf(userFromRedis.get("token")));
        } else {
            userFromRedis.set("token", userDTO.getToken());
        }
        if (StrUtil.isBlank(userDTO.getUserFace())) { // 取userFace信息
            userDTO.setUserFace(String.valueOf(userFromRedis.get("userFace")));
        } else {
            userFromRedis.set("userFace", userDTO.getUserFace());
        }
    }

    /*邮箱格式验证方法*/
    public Boolean checkEmail(String email) {
        String email_reg = "^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
        return email.matches(email_reg); // 检验邮箱格式
    }

    /* 用户检查方法，根据指定的字段和值返回对应操作所需的用户对象 */
    public User checkUser(String action, String attr, String key) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>(); // 实例化一个包装查询器
        queryWrapper.eq(attr, key); // 定义查询规则
        long count = userMapper.selectCount(queryWrapper);

        switch (action) { // 分支返回检查结果
            case StringConst.SEND_EMAIL: // 邮件发送(只需判断是否存在即可)
            case StringConst.MODIFY_EMAIL: // 邮件换绑(只需判断是否存在即可)
            case StringConst.USER_REGISTER: // 注册操作需判断用户是否已存在
                if (count != 0) {
                    return new User(); // 用户已存在会返回空对象
                }
                return null; // 不存在会返回null
            case StringConst.USERNAME_LOGIN: // 用户名登录
            case StringConst.EMAIL_LOGIN: // 用户邮箱登录
            case StringConst.PWD_RESET: // 用户密码重置
                if (count == 0) { // 用户不存在
                    throw new ServiceException(Constants.CODE_600, "用户不存在");
                } else if (count > 1) { // 存在多个用户属于系统异常(正常来说不会出现)
                    throw new ServiceException(Constants.CODE_500, "用户数据异常");
                }
                return userMapper.selectOne(queryWrapper); // 正常会返回指定对象
            default:
                return null;
        }
    }

    /*验证邮箱与验证码的正确性*/
    public Boolean checkEmailCode(String email, String code) {
        if (!checkEmail(email)) { // 先检验邮箱格式
            throw new ServiceException(Constants.CODE_400, "邮箱格式有误"); // 格式不正确时抛出异常
        }
        String codeFromRedis = stringRedisTemplate.opsForValue().get(StringConst.CODE_EMAIL + email);
        if ("".equals(codeFromRedis) || codeFromRedis == null) { // 检验redis有没有对应的验证码
            throw new ServiceException(Constants.CODE_600, "请先获取验证码");
        } else {
            return code.equals(codeFromRedis);
        }
    }
}
