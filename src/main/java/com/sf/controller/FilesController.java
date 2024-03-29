package com.sf.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.common.Result;
import com.sf.entity.Files;
import com.sf.service.IFilesService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

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
    public Result<String> upload(@RequestParam MultipartFile file) throws IOException {


        return Result.success(fileService.uploadFile(file));
    }

    /**
     * 下载文件
     *
     * @param fileId
     * @param response
     * @throws IOException
     */

    @ApiOperation(value = "文件下载")
    @GetMapping("/download/{fileId}")
    public void download(@ApiParam(name = "fileId", value = "file的id", required = true) @PathVariable Integer fileId, HttpServletResponse response) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();

        byte[] bytes = fileService.downloadFile(fileId, outputStream);

        response.addHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileService.getById(fileId).getName(), "UTF-8"));
        response.setContentType("application/octet-stream");

        //通过输出流返回文件
        outputStream.write(bytes);
        outputStream.flush();
        outputStream.close();


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


    @ApiOperation(value = "分页查找", notes = "支持文件名，是否启用（默认所有），类型")
    @GetMapping("/page")
    public Result<IPage<Files>> findPage(@ApiParam(name = "pageNum", value = "当前页码", required = true) @RequestParam Integer pageNum,
                                         @ApiParam(name = "pageSize", value = "页面大小", required = true) @RequestParam Integer pageSize,
                                         @ApiParam(name = "files", value = "file对象") Files files
    ) {
        IPage<Files> page = fileService.getPage(pageNum, pageSize, files);
        if (pageNum > page.getPages()) {
            page = fileService.getPage((int) page.getPages(), pageSize, files);
        }
        return Result.success(page);
    }
}
