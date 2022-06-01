package com.sf.controller;

import cn.hutool.core.io.IORuntimeException;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.common.Result;
import com.sf.entity.dto.UserDTO;
import com.sf.utils.OBSUtils;
import io.swagger.annotations.*;
import org.apache.poi.ss.formula.functions.T;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
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


    @ApiOperation(value = "新增/修改接口")
    @PostMapping
    public Result<Void> save(@RequestBody Files file) {
        fileService.saveOrUpdate(file);
        return Result.success();
    }


    @ApiOperation(value = "根据id删除")
    @DeleteMapping("/{fileId}")
    public Result<Void> delete(@ApiParam(name = "fileId", value = "删除id", required = true) @PathVariable Integer fileId) {

        fileService.deleteById(fileId);
        return Result.success();
    }


    @ApiOperation(value = "批量删除")
    @PostMapping("/del/batch")
    public Result<Void> deleteBatch(@ApiParam(name = "fileIdList", value = "删除ids", required = true) @RequestBody List<Integer> fileIdList) {
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
    @ApiOperation(value = "文件上传")
    @PostMapping("/upload")
    public Result<Void> upload(@RequestParam MultipartFile file) throws IOException {

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
    @GetMapping("/download/{fileUUID}")
    public void download(@ApiParam(name = "fileUUID", value = "file的名称", required = true) @PathVariable String fileUUID, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        //根据文件唯一标识码获取文件
        //File uploadFile = new File(StringConst.BASE_URL + fileUUID);

        //String fileUrl = StringConst.BASE_URL + fileUUID;
        String fileUrl = fileUUID;
        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileUUID, "UTF-8"));
        response.setContentType("application/octet-stream");

        try {
            ////通过文件路径读取文件字节流
            //byte[] bytes = FileUtil.readBytes(uploadFile);
            ////通过输出流返回文件

            //

            ByteArrayOutputStream byteArrayOutputStream = OBSUtils.downloadFile(fileUrl);
            byte[] bytes = byteArrayOutputStream.toByteArray();
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IORuntimeException e) {
            log.warn("没有找到文件");

        } finally {
            assert outputStream != null;
            outputStream.close();
        }

    }

    @ApiOperation(value = "根据id查找一个")
    @GetMapping("/{id}")
    public Result<Files> findOne(@ApiParam(name = "id", value = "文件id", required = true) @PathVariable Integer id) {
        return Result.success(fileService.getById(id));
    }


    @ApiOperation(value = "查找所有")
    @GetMapping
    public Result<List<Files>> findAll() {
        return Result.success(fileService.findAllFile());
    }


    @ApiOperation(value = "分页查找")
    @GetMapping("/page")
    public Result<IPage<Files>> findPage(@ApiParam(name = "pageNum", value = "当前页码", required = true) @RequestParam Integer pageNum,
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
