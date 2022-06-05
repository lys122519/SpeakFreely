package com.sf.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.entity.Comment;
import com.sf.entity.dto.CommentDto;
import com.sf.mapper.CommentMapper;
import com.sf.mapper.UserMapper;
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
@Transactional(rollbackFor = Exception.class)
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements ICommentService {
    private static final Logger log = LoggerFactory.getLogger(CommentServiceImpl.class);

    @Resource
    private CommentMapper commentMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Resource
    private UserMapper userMapper;

    /**
     * 保存评论
     *
     * @param commentDto
     */
    @Override
    public void saveComment(CommentDto commentDto) {

        commentDto.setUserId(RedisUtils.getCurrentUserId(TokenUtils.getToken()));
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
        commentDto.setUserId(RedisUtils.getCurrentUserId(TokenUtils.getToken()));

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

    }

    /**
     * 根据id批量删除
     *
     * @param ids
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatchByIds(List<Integer> ids) {

        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();

        //循环ids数组删除pid=id或origin_id=id
        for (Integer id : ids) {
            queryWrapper.eq("pid", id).or().eq("origin_id", id);
            commentMapper.delete(queryWrapper);
        }

        commentMapper.deleteBatchIds(ids);

    }

    /**
     * 查找用户所有评论
     *
     * @param page
     * @return
     */
    @Override
    public List<Comment> findUserComment(Page<List<Comment>> page) {
        Integer currentUserId = RedisUtils.getCurrentUserId(TokenUtils.getToken());

        //当前用户的所有评论
        List<Comment> comments = commentMapper.selectUserComment(page, currentUserId);

        //当前用户父级不为空的评论
        List<Comment> originList = comments.stream().filter(comment -> (comment.getOriginId() != null
                && comment.getPid() != null)).collect(Collectors.toList());

        for (Comment comment : originList) {

            Integer pid = comment.getPid();
            if (pid != null) {
                //查找到父级评论
                Comment pComment = commentMapper.selectById(pid);
                Integer userId = pComment.getUserId();
                String nickname = userMapper.selectById(userId).getNickname();

                //将父级id和昵称赋值给当前comment
                comment.setpNickName(nickname);
                comment.setpUserId(userId);
            }


        }


        return comments;

    }
}
