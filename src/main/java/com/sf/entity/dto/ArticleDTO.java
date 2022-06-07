package com.sf.entity.dto;

import com.sf.enums.ArticleEnum;

public class ArticleDTO {
    private Integer id;
    private String name;
    private String content;
    private String time;
    private Boolean deleted;
    private ArticleEnum enabled;
    private Integer userId;
    private String authorNickname;
    private String authorAvatarUrl;
    private String tagsID;
    private String tagsContent;
    private String tagsCounts;
    private Integer searchTagID;
    private String searchArticleTitle;

    public Integer getSearchTagID() {
        return searchTagID;
    }

    public void setSearchTagID(Integer searchTagID) {
        this.searchTagID = searchTagID;
    }

    public String getSearchArticleTitle() {
        return searchArticleTitle;
    }

    public void setSearchArticleTitle(String searchArticleTitle) {
        this.searchArticleTitle = searchArticleTitle;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public ArticleEnum getEnabled() {
        return enabled;
    }

    public void setEnabled(ArticleEnum enabled) {
        this.enabled = enabled;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getAuthorNickname() {
        return authorNickname;
    }

    public void setAuthorNickname(String authorNickname) {
        this.authorNickname = authorNickname;
    }

    public String getAuthorAvatarUrl() {
        return authorAvatarUrl;
    }

    public void setAuthorAvatarUrl(String authorAvatarUrl) {
        this.authorAvatarUrl = authorAvatarUrl;
    }

    public String getTagsID() {
        return tagsID;
    }

    public void setTagsID(String tagsID) {
        this.tagsID = tagsID;
    }

    public String getTagsContent() {
        return tagsContent;
    }

    public void setTagsContent(String tagsContent) {
        this.tagsContent = tagsContent;
    }

    public String getTagsCounts() {
        return tagsCounts;
    }

    public void setTagsCounts(String tagsCounts) {
        this.tagsCounts = tagsCounts;
    }

}