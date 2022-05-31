package com.sf.mapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.entity.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {
    /**
     *
     * 根据名称查找
     * @param page
     * @param name
     * @return
     */
    Page<Article> findPage(Page<Article> page, String name);
}
