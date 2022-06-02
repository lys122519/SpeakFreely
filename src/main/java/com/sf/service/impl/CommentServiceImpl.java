package com.sf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sf.entity.Comment;
import com.sf.entity.dto.CommentDto;
import com.sf.mapper.CommentMapper;
import com.sf.service.ICommentService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.utils.RedisUtils;
import com.sf.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
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

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void saveComment(CommentDto commentDto) {
        commentDto.setUserId(RedisUtils.getCurrentUserId(commentDto.getToken()));
        commentDto.setTime(DateUtil.now());


        if (commentDto.getPid() != null) {
            //如果是回复，才处理
            Integer pid = commentDto.getPid();
            Comment pComment = commentMapper.selectById(pid);
            if (pComment.getOriginId() != null) {
                //当前回复的评论有祖宗，则设置相同的祖宗
                commentDto.setOriginId(pComment.getOriginId());
            } else {
                //设置父级为当前的祖宗
                commentDto.setOriginId(commentDto.getPid());
            }
        }
        Comment comment = new Comment();
        BeanUtil.copyProperties(commentDto, comment, true);
        commentMapper.insert(comment);
    }

    /**
     * 修改评论内容
     *
     * @param commentDto
     */
    @Override
    public void updateComment(CommentDto commentDto) {
        commentDto.setUserId(RedisUtils.getCurrentUserId(commentDto.getToken()));

        Comment comment = new Comment();
        BeanUtil.copyProperties(commentDto, comment, true);
        commentMapper.updateById(comment);
    }

    /**
     * 根据id查找评论及回复
     *
     * @param articleId
     * @return
     */
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

    /**
     * 根据评论id删除评论及回复
     *
     * @param id
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteById(Integer id) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id).or().eq("pid", id).or().eq("origin_id", id);
        commentMapper.delete(queryWrapper);

        //commentMapper.deleteById(id);
        //
        //QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        ////级联删除
        //queryWrapper.eq("pid", id).or().eq("origin_id", id);
        //List<Comment> comments = commentMapper.selectList(queryWrapper);
        //for (Comment comment : comments) {
        //    commentMapper.deleteById(comment);
        //}

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchByIds(List<Integer> ids) {
        commentMapper.deleteBatchIds(ids);
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        List<Comment> commentList = commentMapper.selectBatchIds(ids);
        Comment comment1 = commentMapper.selectById(ids.get(1));

        List<Comment> cascadeList = commentList.stream().filter(comment -> (comment.getOriginId() != null && comment.getPid() != null)).collect(Collectors.toList());
        commentMapper.deleteBatchIds(cascadeList);
        //for (Integer id : ids) {
        //    QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        //    queryWrapper.eq("id", id).or().eq("pid", id).or().eq("origin_id", id);
        //    commentMapper.delete(queryWrapper);
        //}


    }


}
