package com.sf.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.entity.Files;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leung
 * @since 2022-05-30
 */
public interface IFilesService extends IService<Files> {
    /**
     * 根据id删除文件
     * @param id
     */
    void deleteById(Integer id);

    /**
     * 根据id批量删除
     * @param ids
     */
    void deleteBatchByIds(List<Integer> ids);


    /**
     * 上传文件
     * @param file
     * @return 文件url
     */
    String uploadFile(MultipartFile file) throws IOException;

    /**
     * 分页查询
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    IPage<Files> getPage(Integer pageNum, Integer pageSize, String name);

    /**
     * 查找所有文件
     * @return
     */
    List<Files>  findAllFile();
}
