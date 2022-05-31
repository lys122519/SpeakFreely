package com.sf.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
public interface IArticleService extends IService<Article> {
    /**
     * 根据文章名称查找
     * @param articlePage
     * @param name
     * @return
     */
    Page<Article> findPage(Page<Article> articlePage, String name);
}
