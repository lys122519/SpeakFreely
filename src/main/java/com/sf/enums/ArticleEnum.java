package com.sf.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @Description:
 * @author: leung
 * @date: 2022-06-03 10:46
 */
public enum ArticleEnum implements IEnum<Integer> {
    /**
     * 未启用
     */
    DISABLE(0, "未启用"),

    /**
     * 启用
     */
    ENABLE(1, "启用");


    @EnumValue
    private int value;

    @JsonValue
    private String desc;

    ArticleEnum(int value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.desc;
    }


}
