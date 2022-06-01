package com.sf.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.sf.common.Constants;
import com.sf.common.StringConst;
import com.sf.entity.User;
import com.sf.entity.dto.UserDTO;
import com.sf.exception.ServiceException;
import com.sf.mapper.UserMapper;
import com.sf.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.utils.TokenUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    @Autowired
    UserMapper userMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private JavaMailSender javaMailSender; // 邮件发送器

    @Value("${spring.mail.username}")
    private String from; // 邮件发送者

    @Override
    public UserDTO userLogin(String username, String password) { // 用户登录
        User user;
        if (checkEmail(username)) { // 用户名格式确定登录方式
            user = checkUser(StringConst.EMAIL_LOGIN, "email", username); // 邮箱登录方式
        } else {
            user = checkUser(StringConst.USERNAME_LOGIN, "username", username); // 用户名登录方式
        }
        if (user != null && password.equals(user.getPassword())) { // 用户存在且密码一致
            UserDTO loginUser = new UserDTO();
            BeanUtil.copyProperties(user, loginUser, true); // 完善返回对象属性

            return setToken(loginUser); // 返回设置token后的用户对象
        } else { // 用户提交信息不匹配
            throw new ServiceException(Constants.CODE_400, "账号或密码错误");
        }
    }

    @Override
    public UserDTO userRegister(UserDTO userDTO) {
        return null;
    }

    @Override
    public UserDTO userInfoModify(UserDTO userDTO) {
        String email = userDTO.getEmail();
        if (!checkEmail(email)) { // 先检验邮箱格式
            throw new ServiceException(Constants.CODE_400, "邮箱格式错误"); // 格式不正确时抛出异常
        }
        String codeFromRedis = stringRedisTemplate.opsForValue().get(StringConst.CODE_EMAIL + email);
        if ("".equals(codeFromRedis) || codeFromRedis == null) { // 检验redis有没有对应的验证码
            throw new ServiceException(Constants.CODE_600, "请先获取验证码");
        } else {
            if (userDTO.getCode().equals(codeFromRedis)) {
                User user = checkUser(StringConst.INFO_MODIFY, "email", email); // 信息修改操作
                BeanUtil.copyProperties(userDTO, user, true); // 通过浅拷贝更新对应用户信息
                userMapper.updateById(user); // 将更新同步到数据库
                // 修改成功，删除redis缓存中对应的验证码信息
                stringRedisTemplate.delete(StringConst.CODE_EMAIL + email);

                return setToken(userDTO);// 更新token并返回
            } else {
                throw new ServiceException(Constants.CODE_600, "验证码不匹配");
            }
        }
    }

    @Override
    public void userPwdReset(String email, String newPwd, String code) {
        if (!checkEmail(email)) { // 先检验邮箱格式
            throw new ServiceException(Constants.CODE_400, "邮箱格式错误"); // 格式不正确时抛出异常
        }
        String codeFromRedis = stringRedisTemplate.opsForValue().get(StringConst.CODE_EMAIL + email);
        if ("".equals(codeFromRedis) || codeFromRedis == null) { // 检验redis有没有对应的验证码
            throw new ServiceException(Constants.CODE_600, "请先获取验证码");
        } else {
            if (code.equals(codeFromRedis)) {
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
            SimpleMailMessage mailMessage = createMail(email, title, content); // 获取对应邮件实例
            javaMailSender.send(mailMessage); // 发送邮件
            //将code放入redis 120s过期
            stringRedisTemplate.opsForValue().set(StringConst.CODE_EMAIL + email, code, 120, TimeUnit.SECONDS);
        } else {
            throw new ServiceException(Constants.CODE_600, "验证码有效期内请勿重复获取");
        }
    }

    /*根据操作标识及邮箱返回邮件标题*/
    public String getEmailTitle(String action, String email) {
        if (action.equals("emailRegister")) { // 注册邮件操作
            if (checkUser("sendEmail", "email", email) == null) { // 用户为空才可创建邮件
                return "用户注册验证码";
            } else { // 注册操作用户已存在则抛出异常
                throw new ServiceException(Constants.CODE_600, "该邮箱已被绑定");
            }
        } else if (checkUser("sendEmail", "email", email) != null) { // 其他操作需判断用户是否存在
            if (action.equals("emailPwdReset")) { // 重置密码邮件
                return "密码重置验证码";
            } else if (action.equals("emailInfoModify")) { // 信息修改邮件
                return "用户信息修改验证码";
            } else {
                throw new ServiceException(Constants.CODE_400, "未知操作");
            }
        } else { // 非注册操作时邮箱对应的用户不存在则抛出异常
            throw new ServiceException(Constants.CODE_600, "用户不存在");
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
        String token = TokenUtils.getToken(userDTO.getId().toString(), userDTO.getPassword());
        userDTO.setToken(token); // 设置用户token
        return userDTO;
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
            case StringConst.SEND_EMAIL: // 邮件发送(只需判断是否存在即可，因此放在此处)
            case StringConst.USER_REGISTER: // 注册操作需判断用户是否已存在
                if (count != 0) {
                    return new User(); // 用户已存在会返回空对象
                }
                return null; // 不存在会返回null
            case StringConst.USERNAME_LOGIN: // 用户名登录
            case StringConst.EMAIL_LOGIN: // 用户邮箱登录
            case StringConst.INFO_MODIFY: // 用户信息修改
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
}
