package com.sf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.entity.Article;
import com.sf.entity.dto.ArticleDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    /**
     * 根据名称查找
     *
     * @param page
     * @param name
     * @return
     */
    Page<Article> findPage(Page<Article> page, @Param("name") String name);

    /**
     * 根据文章id获取单个文章信息(包含作者和标签信息),未被删除,且已被启用(已发布)
     */
    ArticleDTO getArticleByID(@Param("articleID") Integer articleID);

    /**
     * 分页文章列表(包含作者和标签信息),未被删除,作者id(可选，null为所有用户),已发布(可选参数enabled,null为所有),标签ID(可选参数tagID，null为所有),文章标题(可选参数articleTitle，null为所有)
     */
    Page<ArticleDTO> pageArticle(Page<ArticleDTO> page, @Param("authorID") Integer authorID, @Param("enabled") Integer enabled, @Param("tagID") Integer tagID, @Param("articleTitle") String title);


    /**
     * 分页获取文章热度排序结果
     */
    Page<ArticleDTO> pageTopArticle(Page<ArticleDTO> page);
}
