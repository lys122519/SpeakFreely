package com.sf.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Constants;
import com.sf.common.Result;
import com.sf.common.StringConst;
import io.swagger.annotations.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.net.URLEncoder;

import com.sf.service.IFilesService;
import com.sf.entity.Files;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author leung
 * @since 2022-05-30
 */
@RestController
@RequestMapping("/files")
@Api(tags = "文件相关接口")
public class FilesController {

    private static final Logger log = LoggerFactory.getLogger(FilesController.class);

    @Resource
    private IFilesService fileService;

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 401, message = "权限不足")
    })
    @ApiOperation(value = "新增/修改接口")
    @PostMapping
    public Result save(@RequestBody Files file) {
        return Result.success(fileService.saveOrUpdate(file));
    }


    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 401, message = "权限不足")
    })
    @ApiOperation(value = "根据id删除")
    @DeleteMapping("/{fileId}")
    public Result delete(@ApiParam(name = "fileId", value = "删除id", required = true) @PathVariable Integer fileId) {

        fileService.deleteById(fileId);
        return Result.success();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "删除成功"),
            @ApiResponse(code = 401, message = "权限不足")
    })
    @ApiOperation(value = "批量删除")
    @PostMapping("/del/batch")
    public Result deleteBatch(@ApiParam(name = "fileIdList", value = "删除ids", required = true) @RequestBody List<Integer> fileIdList) {
        fileService.deleteBatchByIds(fileIdList);
        return Result.success();
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    @ApiResponses({
            @ApiResponse(code = 200, message = "上传成功"),
            @ApiResponse(code = 600, message = "上传至OBS失败"),
            @ApiResponse(code = 401, message = "权限不足")
    })
    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public Result upload(@RequestParam MultipartFile file) throws IOException {

        fileService.uploadFile(file);
        return Result.success();
    }

    /**
     * 下载文件
     *
     * @param fileUUID
     * @param response
     * @throws IOException
     */

    @ApiOperation(value = "文件下载")
    @GetMapping("/{fileUUID}")
    public void download(@ApiParam(name = "fileUUID", value = "file的名称", required = true) @PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream;
        //根据文件唯一标识码获取文件
        File uploadFile = new File(StringConst.BASE_URL + fileUUID);

        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");

        //通过文件路径读取文件字节流
        byte[] bytes = FileUtil.readBytes(uploadFile);
        //通过输出流返回文件
        outputStream = response.getOutputStream();
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查找成功"),
            @ApiResponse(code = 401, message = "权限不足")
    })
    @ApiOperation(value = "根据id查找一个")
    @GetMapping("/{id}")
    public Result findOne(@ApiParam(name = "id", value = "文件id", required = true) @PathVariable Integer id) {
        return Result.success(fileService.getById(id));
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查找成功"),
            @ApiResponse(code = 401, message = "权限不足")
    })
    @ApiOperation(value = "查找所有")
    @GetMapping
    public Result findAll() {
        return Result.success(fileService.list());
    }

    @ApiResponses({
            @ApiResponse(code = 200, message = "查找成功"),
            @ApiResponse(code = 401, message = "权限不足")
    })
    @ApiOperation(value = "分页查找")
    @GetMapping("/page")
    public Result findPage(@ApiParam(name = "pageNum", value = "当前页码", required = true) @RequestParam Integer pageNum,
                           @ApiParam(name = "pageSize", value = "页面大小", required = true) @RequestParam Integer pageSize,
                           @ApiParam(name = "fileName", value = "文件名称", required = true) @RequestParam(defaultValue = "") String fileName
    ) {
        IPage<Files> page = fileService.getPage(pageNum, pageSize, fileName);
        if (pageNum > page.getPages()) {
            page = fileService.getPage((int) page.getPages(), pageSize, fileName);
        }
        return Result.success(page);
    }
}
