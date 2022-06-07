package com.sf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.entity.Article;
import com.sf.entity.Tags;
import com.sf.entity.TagsArticle;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Description: 文章标签操纵mapper
 * @author: leung
 * @date: 2022-06-03 10:23
 */
@Mapper
public interface TagsArticleMapper extends BaseMapper<TagsArticle> {
    /**
     * 根据文章id查询标签
     */
    @Select("SELECT * FROM tb_tags " +
            "where id in " +
            "(select tag_id from tb_tag_article " +
            "where article_id=#{articleID})")
    List<Tags> getTagsByArticle(@Param("articleID") Integer articleID);

    /**
     * 根据标签id查询文章
     */
    @Select("SELECT * FROM tb_article " +
            "where id in " +
            "(select article_id from tb_tag_article " +
            "where tag_id=#{tagID})")
    List<Article> getArticlesByTag(@Param("tagID") Integer tagID);

    /**
     * 批量插入tagsArticle数据(根据articleID和tagID列表)
     */
    void batchAddList(@Param("articleID") Integer articleID, @Param("samples") List<Integer> samples);

    /**
     * 批量删除tagsArticle数据(根据articleID和tagID列表)
     */
    void batchRemoveList(@Param("articleID") Integer articleID, @Param("samples") List<Integer> samples);
}
