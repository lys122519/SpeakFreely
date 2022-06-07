package com.sf.mapper;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.entity.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.entity.dto.CommentDto;
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
public interface CommentMapper extends BaseMapper<Comment> {
    /**
     * 找出评论回复
     *
     * @param articleId
     * @return
     */
    List<Comment> findCommentDetail(@Param("articleId") Integer articleId);

    /**
     * 查找用户所有评论
     *
     * @param page
     * @param currentUserId
     * @return
     */
    List<Comment> selectUserComment(@Param("currentUserId") Integer currentUserId);

    /**
     * 分页查找
     *
     * @param page
     * @param content
     */
    IPage<CommentDto> findPage(IPage<Comment> page, @Param("content") String content);
}
