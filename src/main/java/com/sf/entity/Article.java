package com.sf.entity;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.annotation.*;
import com.sf.enums.ArticleEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 *
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
@TableName("tb_article")
@ApiModel(value = "Article对象", description = "")
public class Article implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("标题")
    private String name;

    @ApiModelProperty("内容")
    private String content;

    @ApiModelProperty("创建时间")
    private String time;

    @ApiModelProperty("是否删除")
    @TableLogic
    private Boolean deleted;

    @ApiModelProperty("是否启用（即是否为草稿）")
    private ArticleEnum enabled;

    @ApiModelProperty("文章热度")
    private Long counts;

    @ApiModelProperty("用户id")
    private Integer userId;

    @ApiModelProperty("文章作者")
    @TableField(exist = false)
    private JSONObject author;

    @ApiModelProperty("文章标签")
    @TableField(exist = false)
    private List<Tags> tags;

    public Long getCounts() {
        return counts;
    }

    public void setCounts(Long counts) {
        this.counts = counts;
    }

    public JSONObject getAuthor() {
        return author;
    }

    public void setAuthor(JSONObject author) {
        this.author = author;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }


    public List<Tags> getTags() {
        return tags;
    }

    public void setTags(List<Tags> tags) {
        this.tags = tags;
    }

    public ArticleEnum getEnabled() {
        return enabled;
    }

    public void setEnabled(ArticleEnum enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "Article{" +
                "id=" + id +
                ", userId=" + userId +
                ", name=" + name +
                ", content=" + content +
                ", time=" + time +
                ", deleted=" + deleted +
                "}";
    }
}
