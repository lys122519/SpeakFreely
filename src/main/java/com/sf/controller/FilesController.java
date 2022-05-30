package com.sf.controller;

import cn.hutool.core.io.FileUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Result;
import com.sf.common.StringConst;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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
 *  前端控制器
 * </p>
 *
 * @author leung
 * @since 2022-05-30
 */
@RestController
@RequestMapping("/files")
@Api(tags = "文件相关接口")
public class FilesController {

    @Resource
    private IFilesService fileService;

    @PostMapping
    @ApiOperation(value = "新增/修改接口")
    public Result save(@RequestBody Files file) {
        return Result.success(fileService.saveOrUpdate(file));
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id删除")
    public Result delete(@PathVariable Integer id) {

        fileService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    @ApiOperation(value = "批量删除")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        fileService.deleteBatchByIds(ids);
        return Result.success();
    }

    /**
     * 上传文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
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
    @GetMapping("/{fileUUID}")
    @ApiOperation(value = "文件下载")
    public void download(@PathVariable String fileUUID, HttpServletResponse response) throws IOException {
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


    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查找一个")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(fileService.getById(id));
    }

    @GetMapping
    @ApiOperation(value = "查找所有")
    public Result findAll() {
        return Result.success(fileService.list());
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查找")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize,
                           @RequestParam(defaultValue = "") String name
    ) {
        IPage<Files> page = fileService.getPage(pageNum, pageSize, name);
        if (pageNum > page.getPages()) {
            page = fileService.getPage((int) page.getPages(), pageSize, name);
        }
        return Result.success(page);
    }
}
