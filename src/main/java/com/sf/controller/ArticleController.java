package com.sf.controller;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Result;
import com.sf.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import com.sf.service.IArticleService;
import com.sf.entity.Article;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
@RestController
@RequestMapping("/article")
@Api(tags = "文章相关接口")
public class ArticleController {
    @Autowired
    private IArticleService articleService;

    @PostMapping
    @ApiOperation(value = "新增/修改接口")
    public Result save(@RequestBody Article article) {

        if (article.getId() == null) {
            //新增
            article.setTime(DateUtil.now());
            article.setUserId(TokenUtils.getCurrentUser().getId());
        }

        return Result.success(articleService.saveOrUpdate(article));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id删除")
    public Result delete(@PathVariable Integer id) {
        return Result.success(articleService.removeById(id));
    }

    @PostMapping("/del/batch")
    @ApiOperation(value = "批量删除")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(articleService.removeBatchByIds(ids));
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查找一个")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(articleService.getById(id));
    }

    @GetMapping
    @ApiOperation(value = "查找所有")
    public Result findAll() {
        return Result.success(articleService.list());
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查找")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name) {

        Page<Article> page = articleService.findPage(new Page<>(pageNum, pageSize), name);
        return Result.success(page);
    }
}
