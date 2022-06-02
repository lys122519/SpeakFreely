package com.sf.common;

/**
 * @Description:
 * @author: leung
 * @date: 2022-03-27 14:15
 */
public interface Constants {
    /**
     * 系统错误
     */
    Integer CODE_500 = 500;
    /**
     * 请求成功
     */
    Integer CODE_200 = 200;

    /**
     * 参数错误
     */
    Integer CODE_400 = 400;

    /**
     * 权限不足
     */
    Integer CODE_401 = 401;

    /**
     * 业务异常
     */
    Integer CODE_600 = 600;

    /**
     * redis用户信息缓存时长设置(单位为秒)
     */
    Integer USER_REDIS_TIMEOUT = 7200;

    /**
     * 用户Token失效
     */
    Integer CODE_999 = 999;
}
