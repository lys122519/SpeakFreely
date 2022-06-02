package com.sf.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Result;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import com.sf.service.ITagsService;
import com.sf.entity.Tags;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author leung
 * @since 2022-06-02
 */
@RestController
@RequestMapping("/tags")

public class TagsController {
    @Autowired
    private ITagsService tagsService;

    @PostMapping
    @ApiOperation(value = "新增/修改接口")
    public Result<Void> save(@RequestBody Tags tags) {
        tagsService.saveOrUpdate(tags);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id删除")
    public Result<Void> delete(@PathVariable Integer id) {
        tagsService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    @ApiOperation(value = "批量删除")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        tagsService.removeBatchByIds(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查找一个")
    public Result<Tags> findOne(@PathVariable Integer id) {
        return Result.success(tagsService.getById(id));
    }

    @GetMapping
    @ApiOperation(value = "查找所有")
    public Result<List<Tags>> findAll() {
        return Result.success(tagsService.list());
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查找")
    public Result<IPage<Tags>> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
        return Result.success(tagsService.page(new Page<>(pageNum, pageSize)));
    }
}
