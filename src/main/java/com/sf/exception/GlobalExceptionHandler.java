package com.sf.exception;

import com.sf.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @Description: 全局异常处理
 * @author: leung
 * @date: 2022-03-27 14:34
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /**
     * 抛出ServiceException 调用该方法
     */
    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result<Void> handleServiceException(ServiceException se) {
        return Result.error(se.getCode(), se.getMessage());
    }


}
