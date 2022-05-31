package com.sf.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.controller.CommentController;
import com.sf.entity.Article;
import com.sf.mapper.ArticleMapper;
import com.sf.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {
    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Resource
    private ArticleMapper articleMapper;

    @Override
    public Page<Article> findPage(Page<Article> page, String name) {
        return articleMapper.findPage(page, name);
    }
}
