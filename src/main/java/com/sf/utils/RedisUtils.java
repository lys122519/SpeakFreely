package com.sf.utils;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description:
 * @author: leung
 * @date: 2022-05-30 22:42
 */
@Component
public class RedisUtils {

    @Resource
    private static StringRedisTemplate stringRedisTemplate;

    /**
     * 设置缓存
     * @param key
     * @param value
     */
    public static void setRedisCache(String key, String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }
}
