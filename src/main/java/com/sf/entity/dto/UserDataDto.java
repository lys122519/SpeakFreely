package com.sf.entity.dto;

import com.sf.enums.SexEnum;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description:
 * @author: leung
 * @date: 2022-06-08 23:08
 */
public class UserDataDto {

    @ApiModelProperty("性别")
    private SexEnum sex;

    @ApiModelProperty("数量")
    private Integer count;

    public SexEnum getSex() {
        return sex;
    }

    public void setSex(SexEnum sex) {
        this.sex = sex;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
