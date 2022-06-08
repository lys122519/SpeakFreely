package com.sf.entity.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description:
 * @author: leung
 * @date: 2022-06-08 16:03
 */
public class DataDto {

    @ApiModelProperty("数据名称")
    private String name;
    @ApiModelProperty("数量")
    private Integer count;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

}
