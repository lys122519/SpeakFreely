package com.sf.service.impl;

import cn.hutool.core.date.DateUtil;
import com.sf.entity.Comment;
import com.sf.mapper.CommentMapper;
import com.sf.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Resource
    private CommentMapper commentMapper;

    @Override
    public void saveComment(Comment comment) {
        comment.setUserId(TokenUtils.getCurrentUser().getId());
        comment.setTime(DateUtil.now());


        if (comment.getPid() != null) {
            //如果是回复，才处理
            Integer pid = comment.getPid();
            Comment pComment = commentMapper.selectById(pid);
            if (pComment.getOriginId() != null) {
                //当前回复的评论有祖宗，则设置相同的祖宗
                comment.setOriginId(pComment.getOriginId());
            } else {
                //设置父级为当前的祖宗
                comment.setOriginId(comment.getPid());
            }
        }
    }

    @Override
    public List<Comment> findReply(Integer articleId) {

        //查询评论及回复
        List<Comment> articleComments = commentMapper.findCommentDetail(articleId);

        //查询评论
        List<Comment> originList = articleComments.stream().filter(comment -> comment.getOriginId() == null).collect(Collectors.toList());

        //设置评论的回复
        for (Comment origin : originList) {
            //comments为回复对象集合
            List<Comment> comments = articleComments.stream().filter(comment -> origin.getId().equals(comment.getOriginId())).collect(Collectors.toList());
            comments.forEach(comment -> {
                Optional<Comment> pComment = articleComments.stream().filter(c1 -> c1.getId().equals(comment.getPid())).findFirst();
                pComment.ifPresent((v -> {
                    //找到父级评论的用户id和用户昵称，并设置给当前回复对象
                    comment.setpUserId(v.getUserId());
                    comment.setpNickName(v.getNickname());

                }));
            });

            origin.setChildren(comments);
        }
        return originList;
    }


}
