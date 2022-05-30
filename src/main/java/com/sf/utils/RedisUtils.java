package com.sf.utils;

import com.sf.service.IUserService;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @Description:
 * @author: leung
 * @date: 2022-05-30 22:42
 */
@Component
public class RedisUtils {


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
}
