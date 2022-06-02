package com.sf.service;

import com.sf.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.entity.dto.CommentDto;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
public interface ICommentService extends IService<Comment> {

    /**
     * 保存评论
     * @param comment
     */
    void saveComment(CommentDto commentDto);

    /**
     * 更新评论
     * @param commentDto
     */
    void updateComment(CommentDto commentDto);



    /**
     * 找到文章评论
     * @param articleId
     * @return
     */
    List<Comment> findReply(Integer articleId);

    /**
     * 根据评论id删除评论及回复
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 批量删除
     * @param ids
     */
    void deleteBatchByIds(List<Integer> ids);
}
