package com.sf.exception;

/**
 * @Description: 自定义异常
 * @author: leung
 * @date: 2022-03-27 14:35
 */
public class ServiceException extends RuntimeException {
    private String code;

    public ServiceException(String code, String msg) {
        super(msg);
        this.code = code;
    }



    public String getCode() {
        return code;
    }
}
