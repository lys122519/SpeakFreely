package com.sf.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 *
 * </p>
 *
 * @author leung
 * @since 2022-06-04
 */
@TableName("tb_report")
@ApiModel(value = "Report对象", description = "")
public class Report implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("举报人ID")
    private Integer userId;

    @ApiModelProperty(value = "违规文章ID", required = true)
    private Integer articleId;

    @ApiModelProperty(value = "违规评论ID", required = true)
    private Integer commentId;

    @ApiModelProperty(value = "举报理由", required = true)
    private String content;

    @ApiModelProperty("举报时间")
    private String time;

    @ApiModelProperty("是否删除")
    private Boolean deleted;

    @ApiModelProperty("是否已处理")
    private Boolean execd;


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

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
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

    public Boolean getExecd() {
        return execd;
    }

    public void setExecd(Boolean execd) {
        this.execd = execd;
    }

    @Override
    public String toString() {
        return "Report{" +
                "id=" + id +
                ", userId=" + userId +
                ", articleId=" + articleId +
                ", commentId=" + commentId +
                ", content=" + content +
                ", time=" + time +
                ", deleted=" + deleted +
                ", execd=" + execd +
                "}";
    }
}
