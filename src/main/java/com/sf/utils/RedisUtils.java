package com.sf.utils;

import com.sf.common.Constants;
import com.sf.controller.ArticleController;
import com.sf.exception.ServiceException;
import com.sf.service.IUserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * @Description:
 * @author: leung
 * @date: 2022-05-30 22:42
 */
@Component
public class RedisUtils {
    private static final Logger log = LoggerFactory.getLogger(RedisUtils.class);


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    private static StringRedisTemplate staticStringRedisTemplate;

    @PostConstruct
    public void setStringRedisTemplate() {
        staticStringRedisTemplate = stringRedisTemplate;
    }


    /**
     * 设置缓存
     *
     * @param key
     * @param value
     */
    public static void setRedisCache(String key, String value) {
        staticStringRedisTemplate.opsForValue().set(key, value);
    }


    /**
     * 获得用户id
     *
     * @param token
     * @return
     */
    public static Integer getCurrentUserId(String token) {

        //从redis中取出当前用户对象
        Map<Object, Object> entries = staticStringRedisTemplate.opsForHash().entries(token);
        int id;
        try {
            id = Integer.parseInt((String) entries.get("id"));
            return id;
        } catch (Exception e) {
            log.info(token);
            throw new ServiceException(Constants.CODE_999, "token验证失败,请重新登录");
        }
    }

    /**
     * 获得redis中用户的某个属性
     *
     * @param token
     * @param attr
     * @return
     */
    public static String getCurrentUserAttr(String token, String attr) {

        //从redis中取出当前用户对象的某个属性
        Map<Object, Object> entries = staticStringRedisTemplate.opsForHash().entries(token);
        String value;
        try {
            value = (String) entries.get(attr);
            return value;
        } catch (Exception e) {
            log.info(token);
            throw new ServiceException(Constants.CODE_999, "token验证失败,请重新登录");
        }
    }

    /**
     * 获得redis中用户Map
     *
     * @param token
     * @return
     */
    public static Map<Object, Object> getUserRedis(String token) {
        //从redis中取出当前用户对象的某个属性
        Map<Object, Object> entries = staticStringRedisTemplate.opsForHash().entries(token);
        if (entries.size() == 0) {// redis中获取不到token对应的用户信息则抛出异常
            throw new ServiceException(Constants.CODE_999, "token验证失败,请重新登录");
        } else {
            return entries;
        }
    }
}
