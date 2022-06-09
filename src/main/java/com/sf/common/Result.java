package com.sf.common;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * @Description: 接口统一返回
 * @author: leung
 * @date: 2022-03-27 14:12
 */
@ApiModel(value = "接口返回对象", description = "接口返回对象")
public class Result<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 操作是否成功
     */
    @ApiModelProperty(notes = "响应码")
    private Integer code;
    /**
     * msg
     */
    @ApiModelProperty(notes = "响应信息")

    private String msg;
    /**
     * 数据
     */
    @ApiModelProperty(notes = "返回结果")
    private T data;

    public Result() {
    }

    public Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 没有数据
     * @return
     */
    public static Result<Void> success() {
        return new Result<Void>(Constants.CODE_200, "", null);
    }

    /**
     * 有数据
     * @param data
     * @return
     */
    public static <V> Result<V> success(V data) {
        return new Result<V>(Constants.CODE_200, "", data);
    }

    public static Result<Void> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    public static Result<Void> error() {
        return new Result<>(Constants.CODE_500, "系统错误", null);
    }


    //public static Result success() {
    //    return new Result(Constants.CODE_200, "", null);
    //}
    //
    //public static Result success(T data) {
    //    return new Result(Constants.CODE_200, "", data);
    //}
    //
    //public static Result error(String code, String msg) {
    //    return new Result(code, msg, null);
    //}
    //
    //public static Result error() {
    //    return new Result(Constants.CODE_500, "系统错误", null);
    //}

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
