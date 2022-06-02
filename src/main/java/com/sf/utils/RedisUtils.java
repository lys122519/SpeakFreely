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
            throw new ServiceException(Constants.CODE_401, "token验证失败,请重新登录");
        }
    }
}
