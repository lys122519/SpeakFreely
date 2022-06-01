package com.sf.service;

import com.sf.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;

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
    void saveComment(Comment comment);

    /**
     * 找到文章评论
     * @param articleId
     * @return
     */
    List<Comment> findReply(Integer articleId);
}
