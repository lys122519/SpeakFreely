package com.sf.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
    @Resource
    private ICommentService commentService;

    @PostMapping
    @ApiOperation(value = "新增/修改接口")
    public Result save(@RequestBody Comment comment) {
        if (comment.getId() == null) {
            //新增
            commentService.saveComment(comment);
        }

        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id删除")
    public Result delete(@PathVariable Integer id) {
        return Result.success(commentService.removeById(id));
    }

    @PostMapping("/del/batch")
    @ApiOperation(value = "批量删除")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(commentService.removeBatchByIds(ids));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查找一个")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(commentService.getById(id));
    }

    @GetMapping
    @ApiOperation(value = "查找所有")
    public Result findAll() {
        return Result.success(commentService.list());
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查找")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        return Result.success(commentService.page(new Page<>(pageNum, pageSize)));
    }

    @GetMapping("/tree/{articleId}")
    @ApiOperation(value = "根据文章id查找评论")
    public Result findTree(@PathVariable Integer articleId) {

        List<Comment> replyList = commentService.findReply(articleId);

        return Result.success(replyList);
    }
}
