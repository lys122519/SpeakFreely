package com.sf.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Result;
import com.sf.entity.Comment;
import com.sf.entity.dto.CommentDto;
import com.sf.service.ICommentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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
    @ApiOperation(value = "查找用户id所有评论", notes = "id不用传")
    public Result<Page<Comment>> findUserCommentById(@ApiParam(name = "pageNum", value = "当前页码", required = true) @RequestParam Integer pageNum,
                                                     @ApiParam(name = "pageSize", value = "页面大小", required = true) @RequestParam Integer pageSize
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
    @ApiOperation(value = "分页查找", notes = "支持按内容查找")
    public Result<IPage<Comment>> findPage(@ApiParam(name = "pageNum", value = "当前页码", required = true) @RequestParam Integer pageNum,
                                           @ApiParam(name = "pageSize", value = "页面大小", required = true) @RequestParam Integer pageSize,
                                           @ApiParam(name = "comment", value = "Comment对象") @RequestBody Comment comment
    ) {
        IPage<Comment> page = commentService.getPage(pageNum, pageSize, comment);
        if (pageNum > page.getPages()) {
            page = commentService.getPage((int) page.getPages(), pageSize, comment);
        }

        return Result.success(page);
    }

    @GetMapping("/tree/{articleId}")
    @ApiOperation(value = "根据文章id查找评论")
    public Result<IPage<Comment>> findTree(@ApiParam(name = "pageNum", value = "当前页码", required = true) @RequestParam Integer pageNum,
                                           @ApiParam(name = "pageSize", value = "页面大小", required = true) @RequestParam Integer pageSize,
                                           @ApiParam(name = "articleId", value = "文章id", required = true) @PathVariable Integer articleId) {

        IPage<Comment> page = commentService.findReply(pageNum, pageSize, articleId);
        if (pageNum > page.getPages()) {
            page = commentService.findReply((int) page.getPages(), pageSize, articleId);
        }


        return Result.success(page);
    }
}
