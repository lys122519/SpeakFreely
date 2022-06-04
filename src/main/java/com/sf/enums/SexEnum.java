package com.sf.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.baomidou.mybatisplus.annotation.IEnum;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author liang
 * @date 2022-01-30 17:16
 */
public enum SexEnum implements IEnum<Integer> {
    /**
     * 女
     */
    WOMAN(0,"女"),
    /**
     * 男
     */
    MAN(1,"男");

    @EnumValue
    private int value;

    @JsonValue
    private String desc;

    SexEnum(int value, String desc) {
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
