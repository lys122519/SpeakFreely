package com.sf.service;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.entity.Article;
import com.sf.entity.dto.ArticleDTO;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
public interface IArticleService extends IService<Article> {
    /**
     * 根据文章名称查找
     *
     * @param articlePage
     * @param name
     * @return
     */
    Page<Article> findPage(Page<Article> articlePage, String name);

    // 文章操作(草稿/发布/修改)接口
    Article articleAction(String action, Article article);

    // 分页查询指定用户文章列表接口
    Page<ArticleDTO> pageArticle(Page<ArticleDTO> articlePage, Integer id, String type, String title);

    // 根据标签id和文章标题(至少一个不为空)分页搜索文章列表
    Page<ArticleDTO> pageSearchArticle(Page<ArticleDTO> articlePage, Integer id, String title);

    // 获取访问量前5个
    List<JSONObject> getTop5();

    // 增加文章访问量
    void addCounts(Integer articleID);
}
