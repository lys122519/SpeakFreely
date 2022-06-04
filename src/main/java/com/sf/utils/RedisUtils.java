package com.sf.utils;

import cn.hutool.json.JSONObject;
import com.sf.common.Constants;
import com.sf.exception.ServiceException;
import org.omg.CORBA.TIMEOUT;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
        //从redis中取出当前用户对象id
        return Integer.valueOf(getCurrentUserAttr(token, "id"));
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
        return getUserRedis(token).get(attr).toString();
    }

    /**
     * 获得redis中用户Map
     *
     * @param token
     * @return
     */
    public static JSONObject getUserRedis(String token) {
        //从redis中取出当前用户对象
        JSONObject user = objFromRedis(token);
        if (user == null) {
            throw new ServiceException(Constants.CODE_999, "token验证失败,请重新登录");
        }
        return user;
    }

    /**
     * 将redis中的单个对象(例如User)存入redis并设置时长(S)
     */
    public static void objToRedis(String key, Object obj, Integer TIMEOUT) {
        staticStringRedisTemplate.opsForValue().set(key, new JSONObject(obj).toString());
        staticStringRedisTemplate.expire(key, TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * 获得redis中的单个对象(例如User),将其还原为json对象格式返回
     */
    public static JSONObject objFromRedis(String key) {
        //从redis中取出单个对象的json字符串缓存，还原为json对象返回
        String obj = staticStringRedisTemplate.opsForValue().get(key);
        if (obj == null) {
            return null;
        }
        return new JSONObject(obj);
    }

    /**
     * 将Map<唯一标识字符串, 对象转json对象再转字符串>存入redis并设置时长(S)
     */
    public static void mapToRedis(String key, Map<Object, Object> mapObject, Integer TIMEOUT) {
        staticStringRedisTemplate.opsForHash().putAll(key, mapObject);
        staticStringRedisTemplate.expire(key, TIMEOUT, TimeUnit.SECONDS);
    }

    /**
     * 获得redis中的一组Map缓存对象(例如Tags)
     */
    public static Map<Object, Object> mapFromRedis(String key) {
        //从redis中取出对应Map缓存
        return staticStringRedisTemplate.opsForHash().entries(key);
    }

    /**
     * 将从redis取出的Map<Object, Object>对象还原成 JSONObject
     */
    public static JSONObject jsonFromMap(Map<Object, Object> mapObject) {
        //从redis中取出的Map为 Map<Object, Object>，将其还原成  JSONObject 字符串返回
        JSONObject resultJson = new JSONObject();
        for (Object key : mapObject.keySet()) {
            resultJson.set((String) key, new JSONObject(mapObject.get(key)));
        }
        return resultJson;
    }
}
