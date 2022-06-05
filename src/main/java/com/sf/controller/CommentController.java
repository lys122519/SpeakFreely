package com.sf.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Result;
import com.sf.entity.dto.CommentDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


import com.sf.service.ICommentService;
import com.sf.entity.Comment;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
@RestController
@RequestMapping("/comment")
@Api(tags = "评论相关接口")
public class CommentController {
    private static final Logger log = LoggerFactory.getLogger(CommentController.class);

    @Resource
    private ICommentService commentService;

    @PostMapping
    @ApiOperation(value = "新增/修改接口")
    public Result<Void> save(@RequestBody CommentDto commentDto) {
        if (commentDto.getId() == null) {
            //新增
            commentService.saveComment(commentDto);
        } else {
            commentService.updateComment(commentDto);
        }

        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id删除评论、子评论及回复")
    public Result<Void> delete(@PathVariable Integer id) {
        commentService.deleteById(id);
        return Result.success();
    }

    @DeleteMapping("/del/batch")
    @ApiOperation(value = "批量删除")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        commentService.deleteBatchByIds(ids);
        return Result.success();
    }

    @GetMapping("/findUserComment")
    @ApiOperation(value = "查找用户所有评论(分页)")
    public Result<List<Comment>> findUserCommentById(@RequestParam Integer pageNum,
                                                     @RequestParam Integer pageSize
    ) {

        return Result.success(commentService.findUserComment(new Page<>(pageNum, pageSize)));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查找一个")
    public Result<Comment> findOne(@PathVariable Integer id) {
        return Result.success(commentService.getById(id));
    }

    @GetMapping
    @ApiOperation(value = "查找所有")
    public Result<List<Comment>> findAll() {
        return Result.success(commentService.list());
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查找")
    public Result<IPage<Comment>> findPage(@RequestParam Integer pageNum,
                                           @RequestParam Integer pageSize) {
        return Result.success(commentService.page(new Page<>(pageNum, pageSize)));
    }

    @GetMapping("/tree/{articleId}")
    @ApiOperation(value = "根据文章id查找评论")
    public Result<List<Comment>> findTree(@PathVariable Integer articleId) {

        List<Comment> replyList = commentService.findReply(articleId);

        return Result.success(replyList);
    }
}
