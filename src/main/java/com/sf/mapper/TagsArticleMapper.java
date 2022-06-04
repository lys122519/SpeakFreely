package com.sf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.entity.Tags;
import com.sf.entity.TagsArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description: 文章标签操纵mapper
 * @author: leung
 * @date: 2022-06-03 10:23
 */
@Mapper
public interface TagsArticleMapper extends BaseMapper<TagsArticle> {
    /**
     * 批量插入tagsArticle数据(根据articleID和tagID列表)
     */
    void batchAddList(@Param("articleID") Integer articleID, @Param("samples") List<Integer> samples);

    /**
     * 批量删除tagsArticle数据(根据articleID和tagID列表)
     */
    void batchRemoveList(@Param("articleID") Integer articleID, @Param("samples") List<Integer> samples);
}
