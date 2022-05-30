package com.sf.exception;

import com.sf.common.Constants;

/**
 * @Description:
 * @author: leung
 * @date: 2022-05-30 17:29
 */
public class ControllerException extends RuntimeException {

    /**
     * 参数错误
     */
    private String code = Constants.CODE_400;

    public ControllerException(String msg) {
        super(msg);

    }

    public String getCode() {
        return code;
    }
}
