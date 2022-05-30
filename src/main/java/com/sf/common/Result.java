package com.sf.common;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description: 接口统一返回
 * @author: leung
 * @date: 2022-03-27 14:12
 */
public class Result {

    /**
     * 操作是否成功
     */
    @ApiModelProperty("状态码")
    private String code;
    /**
     * msg
     */
    @ApiModelProperty("信息")
    private String msg;
    /**
     * 数据
     */
    @ApiModelProperty("数据")
    private Object data;

    public Result() {
    }

    public Result(String code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public static Result success() {
        return new Result(Constants.CODE_200, "", null);
    }

    public static Result success(Object data) {
        return new Result(Constants.CODE_200, "", data);
    }

    public static Result error(String code, String msg) {
        return new Result(code, msg, null);
    }

    public static Result error() {
        return new Result(Constants.CODE_500, "系统错误", null);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
