package com.sf.entity.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description:
 * @author: leung
 * @date: 2022-06-05 20:02
 */
public class InterfaceDto implements Comparable<InterfaceDto> {

    @ApiModelProperty("接口名称")
    private String name;

    @ApiModelProperty("接口使用次数")
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
    
    @Override
    public int compareTo(InterfaceDto o) {
        return this.count.compareTo(o.count);
    }
}
