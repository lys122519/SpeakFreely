package com.sf.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 *
 * @author leung
 * @since 2022-06-06
 */
@TableName("tb_active_user")
@ApiModel(value = "ActiveUser对象", description = "")
public class ActiveUser implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("记录id")
    private Integer id;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("访问时间")
    private String time;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "ActiveUser{" +
                "id=" + id +
                ", userId=" + userId +
                ", time=" + time +
                "}";
    }
}
