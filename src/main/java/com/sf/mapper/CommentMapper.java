package com.sf.mapper;

import com.sf.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 找出评论回复
     * @param articleId
     * @return
     */
    List<Comment> findCommentDetail(@Param("articleId") Integer articleId);
}