package com.sf.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModelProperty;

/**
 * @Description:
 * @author: leung
 * @date: 2022-06-07 13:44
 */
public class ReportDto {

    @ApiModelProperty("ID")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("举报人ID")
    private Integer userId;

    @ApiModelProperty("举报人名称")
    @TableField(exist = false)
    private String nickName;

    @ApiModelProperty("举报时间")
    private String time;

    @ApiModelProperty(value = "违规文章ID")
    private Integer articleId;

    @ApiModelProperty(value = "违规文章标题")
    @TableField(exist = false)
    private String articleName;

    @ApiModelProperty(value = "违规评论ID")
    private Integer commentId;

    @ApiModelProperty(value = "违规评论内容")
    @TableField(exist = false)
    private String commentContent;

    @ApiModelProperty(value = "举报理由")
    private String content;

    @ApiModelProperty("是否已处理")
    private Boolean execd;

    @ApiModelProperty("是否删除")
    private Boolean deleted;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public String getArticleName() {
        return articleName;
    }

    public void setArticleName(String articleName) {
        this.articleName = articleName;
    }

    public Integer getCommentId() {
        return commentId;
    }

    public void setCommentId(Integer commentId) {
        this.commentId = commentId;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getExecd() {
        return execd;
    }

    public void setExecd(Boolean execd) {
        this.execd = execd;
    }
}
