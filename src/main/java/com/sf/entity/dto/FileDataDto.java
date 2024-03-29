package com.sf.entity.dto;

import io.swagger.annotations.ApiModelProperty;

/**
 * @Description:
 * @author: leung
 * @date: 2022-06-07 15:58
 */
public class FileDataDto {

    @ApiModelProperty("文件类型")
    private String type;

    @ApiModelProperty("数量")
    private Integer count;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
