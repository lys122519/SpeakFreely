package com.sf.entity.dto;

/**
 * @Description:
 * @author: leung
 * @date: 2022-06-05 20:02
 */
public class InterfaceDto implements Comparable<InterfaceDto> {
    private String name;
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
