package com.sf.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.entity.Comment;
import com.sf.entity.dto.CommentDto;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
public interface ICommentService extends IService<Comment> {

    /**
     * 保存评论
     *
     * @param comment
     */
    void saveComment(CommentDto commentDto);

    /**
     * 更新评论
     *
     * @param commentDto
     */
    void updateComment(CommentDto commentDto);


    /**
     * 找到文章评论
     *
     * @param articleId
     * @return
     */
    IPage<Comment> findReply(Integer pageNum, Integer pageSize, Integer articleId);

    /**
     * 根据评论id删除评论及回复
     *
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 批量删除
     *
     * @param ids
     */
    void deleteBatchByIds(List<Integer> ids);

    /**
     * 查找用户所有评论
     *
     * @param page
     */
    Page<CommentDto> findUserComment(Page<CommentDto> page);

    /**
     * 分页查找
     *
     * @param pageNum
     * @param pageSize
     * @param comment
     * @return
     */
    IPage<CommentDto> getPage(Integer pageNum, Integer pageSize, CommentDto commentDto);

}
