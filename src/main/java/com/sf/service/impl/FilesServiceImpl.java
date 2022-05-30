package com.sf.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.StringConst;
import com.sf.entity.Files;
import com.sf.mapper.FilesMapper;
import com.sf.service.IFilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.utils.OBSUtils;
import com.sf.utils.RedisUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leung
 * @since 2022-05-30
 */
@Service
public class FilesServiceImpl extends ServiceImpl<FilesMapper, Files> implements IFilesService {

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Resource
    private FilesMapper fileMapper;

    @Override
    public void deleteById(Integer id) {
        fileMapper.deleteById(id);

        setFilesRedisCache();
    }

    @Override
    public void deleteBatchByIds(List<Integer> ids) {
        fileMapper.deleteBatchIds(ids);

        setFilesRedisCache();
    }

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String type = FileUtil.extName(originalFilename);
        long size = file.getSize();

        //存储到磁盘
        File uploadParentFile = new File(fileUploadPath);

        //判断配置的文件目录是否存在，不存在则创造新的文件目录
        if (!uploadParentFile.exists()) {
            uploadParentFile.mkdirs();
        }

        // 定义一个文件的唯一标识码
        String uuid = IdUtil.fastSimpleUUID();

        // 文件名加后缀
        String fileUUID = uuid + StrUtil.DOT + type;
        File uploadFile = new File(fileUploadPath + fileUUID);

        //文件最终URL
        String finalUrl = "";
        // 将获取到的文件存储到磁盘目录
        file.transferTo(uploadFile);
        //获取文件md5
        String fileMD5 = SecureUtil.md5(uploadFile);

        //根据md5查询是否有重复文件
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", fileMD5);
        List<Files> filesList = fileMapper.selectList(queryWrapper);

        String urlFromSql = null;
        if (filesList.size() != 0) {
            urlFromSql = filesList.get(0).getUrl();
        }


        //从数据库查不到相同的md5，则上传，否则将已有文件的url其该文件最终url
        if (urlFromSql != null) {
            finalUrl += urlFromSql;
            //相同md5的文件存在，删除已保存文件
            uploadFile.delete();
        } else {
            //无相同md5文件，则上传
            finalUrl = StringConst.BASE_URL + fileUUID;
            OBSUtils.uploadFile(fileUUID, uploadFile.getPath());

        }

        // 存储数据库
        Files saveFile = new Files();
        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size / 1024);
        saveFile.setUrl(finalUrl);
        saveFile.setMd5(fileMD5);
        fileMapper.insert(saveFile);

        setFilesRedisCache();

        return finalUrl;
    }

    /**
     * 分页查询
     *
     * @param pageNum
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public IPage<Files> getPage(Integer pageNum, Integer pageSize, String name) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("id");
        if (!"".equals(name)) {
            queryWrapper.like("name", name);
        }
        IPage<Files> page = new Page<>(pageNum, pageSize);
        fileMapper.selectPage(page, queryWrapper);
        return page;
    }

    /**
     * 设置Redis缓存
     */
    private void setFilesRedisCache() {
        //未删除的加入缓存
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("deleted", 0);
        //设置缓存
        List<Files> list = fileMapper.selectList(queryWrapper);
        RedisUtils.setRedisCache(StringConst.FILE_KEY, JSONUtil.toJsonStr(list));
    }
}
