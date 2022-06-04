package com.sf.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import com.sf.service.IReportService;
import com.sf.entity.Report;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author leung
 * @since 2022-06-04
 */
@RestController
@RequestMapping("/report")
@Api(tags = "举报相关接口")
public class ReportController {
    @Autowired
    private IReportService reportService;

    @PostMapping
    @ApiOperation(value = "新增/修改接口")
    public Result<Void> save(@RequestBody Report report) {
        reportService.saveOrUpdate(report);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id删除")
    public Result<Void> delete(@PathVariable Integer id) {
        reportService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    @ApiOperation(value = "批量删除")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        reportService.removeBatchByIds(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查找一个")
    public Result<Report> findOne(@PathVariable Integer id) {
        return Result.success(reportService.getById(id));
    }

    @GetMapping
    @ApiOperation(value = "查找所有")
    public Result<List<Report>> findAll() {
        return Result.success(reportService.list());
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查找")
    public Result<IPage<Report>> findPage(@RequestParam Integer pageNum,
                                          @RequestParam Integer pageSize) {
        return Result.success(reportService.page(new Page<>(pageNum, pageSize)));
    }
}
