package com.sf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.entity.Tags;
import com.sf.entity.TagsArticle;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description: 文章标签操纵mapper
 * @author: leung
 * @date: 2022-06-03 10:23
 */
@Mapper
public interface TagsArticleMapper extends BaseMapper<TagsArticle> {
}
