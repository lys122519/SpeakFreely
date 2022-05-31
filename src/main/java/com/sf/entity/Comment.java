package com.sf.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.List;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * <p>
 *
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
@TableName("tb_comment")
@ApiModel(value = "Comment对象", description = "")
public class Comment implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty("id")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty("评论用户id")
    private Integer userId;

    @ApiModelProperty("评论内容")
    private String content;

    @ApiModelProperty("评论时间")
    private String time;

    @ApiModelProperty("评论父id")
    private Integer pid;

    @ApiModelProperty("最上级id")
    private Integer originId;

    @ApiModelProperty("所属文章id")
    private Integer articleId;

    @ApiModelProperty("是否删除")
    @TableLogic
    private Boolean deleted;

    @ApiModelProperty("父节点用户id")
    @TableField(exist = false)
    private Integer pUserId;

    @ApiModelProperty("父节点用户昵称")
    @TableField(exist = false)
    private String pNickName;

    @ApiModelProperty("用户昵称")
    @TableField(exist = false)
    private String nickname;

    @ApiModelProperty("用户头像")
    @TableField(exist = false)
    private String avatarUrl;

    @ApiModelProperty("评论回复")
    @TableField(exist = false)
    private List<Comment> children;

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

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getOriginId() {
        return originId;
    }

    public void setOriginId(Integer originId) {
        this.originId = originId;
    }

    public Integer getArticleId() {
        return articleId;
    }

    public void setArticleId(Integer articleId) {
        this.articleId = articleId;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Integer getpUserId() {
        return pUserId;
    }

    public void setpUserId(Integer pUserId) {
        this.pUserId = pUserId;
    }

    public String getpNickName() {
        return pNickName;
    }

    public void setpNickName(String pNickName) {
        this.pNickName = pNickName;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public List<Comment> getChildren() {
        return children;
    }

    public void setChildren(List<Comment> children) {
        this.children = children;
    }

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", userId=" + userId +
                ", content=" + content +
                ", time=" + time +
                ", pid=" + pid +
                ", originId=" + originId +
                ", articleId=" + articleId +
                ", deleted=" + deleted +
                "}";
    }
}
