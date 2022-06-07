package com.sf.config.interceptor;

import cn.hutool.core.util.StrUtil;
import com.sf.common.Constants;
import com.sf.config.AuthAccess;
import com.sf.exception.ServiceException;
import com.sf.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @author: leung
 * @date: 2022-03-28 14:34
 */

public class JwtInterceptor implements HandlerInterceptor {
    private static final Logger log = LoggerFactory.getLogger(JwtInterceptor.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("token");

        // 如果不映射到方法直接通过
        if (!(handler instanceof HandlerMethod)) {
            return true;
        } else {
            //包含@AuthAccess 放行
            HandlerMethod h = (HandlerMethod) handler;
            AuthAccess authAccess = h.getMethodAnnotation(AuthAccess.class);
            if (authAccess != null) {
                return true;
            }
        }

        // 执行认证
        if (StrUtil.isBlank(token)) {
            throw new ServiceException(Constants.CODE_401, "无token，请重新登录");
        }

        RedisUtils.getUserRedis(token);
        //如果能查到 重置过期时长
        stringRedisTemplate.expire(token, Constants.USER_REDIS_TIMEOUT, TimeUnit.SECONDS);
        return true;
    }
}
