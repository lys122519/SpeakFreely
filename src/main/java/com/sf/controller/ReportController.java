package com.sf.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.common.Result;
import com.sf.entity.Report;
import com.sf.entity.dto.ReportDto;
import com.sf.service.IReportService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        if (report.getId() == null) {
            reportService.saveReport(report);
        } else {
            reportService.updateReport(report);
        }

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


    @GetMapping("/page")
    @ApiOperation(value = "分页查找", notes = "根据举报时间降序(根据内容，用户昵称，是否已处理)")
    public Result<IPage<ReportDto>> findPage(@ApiParam(name = "pageNum", value = "当前页码", required = true) @RequestParam Integer pageNum,
                                             @ApiParam(name = "pageSize", value = "页面大小", required = true) @RequestParam Integer pageSize,
                                             ReportDto reportDto
    ) {
        IPage<ReportDto> page = reportService.getPage(pageNum, pageSize, reportDto);
        if (pageNum > page.getPages()) {
            page = reportService.getPage((int) page.getPages(), pageSize, reportDto);
        }

        return Result.success(page);
    }
}
