package com.sf.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sf.entity.User;
import com.sf.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Objects;

/**
 * @Description:
 * @author: leung
 * @date: 2022-03-28 14:12
 */
@Component
public class TokenUtils {

    private static IUserService staticUserService;
    @Resource
    private IUserService userService;


    @PostConstruct
    public void setUserService() {
        staticUserService = userService;
    }

    /**
     * 生成token
     *
     * @param userId
     * @return
     */
    public static String getToken(String userId) {

        return JWT.create()
                //将userid保存到token中，作为载荷
                .withAudience(userId)
                //2小时后过期
                .withExpiresAt(DateUtil.offsetHour(new Date(), 2))
                //将16会随机数+sign+时间戳作为token密钥
                .sign(Algorithm.HMAC256(RandomUtil.randomNumbers(16) + userId + DateUtil.date()));
    }


    ///**
    // * 生成token
    // *
    // * @param userId
    // * @param sign   密码
    // * @return
    // */
    //public static String getToken(String userId, String sign) {
    //
    //    return JWT.create()
    //            //将userid保存到token中，作为载荷
    //            .withAudience(userId)
    //            //2小时后过期
    //            .withExpiresAt(DateUtil.offsetHour(new Date(), 2))
    //            //将sign+时间戳+作为token密钥
    //            .sign(Algorithm.HMAC256(DateUtil.date() + RandomUtil.randomNumbers(16)));
    //}

    /**
     * 获得header中的token
     *
     * @return
     */
    public static String getToken() {
        // 通过RequestContextHolder获得到当前request
        HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        return request.getHeader("token");
    }


    /**
     * 获取当前登录用户信息
     *
     * @param
     * @return
     */
    public static User getCurrentUser() {
        try {
            // 通过RequestContextHolder获得到当前request
            HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
            String token = request.getHeader("token");
            if (StrUtil.isNotBlank(token)) {
                String userId = JWT.decode(token).getAudience().get(0);
                return staticUserService.getById(Integer.valueOf(userId));
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }

    }
}
