package com.sf.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.obs.services.model.PutObjectResult;
import com.sf.common.Constants;
import com.sf.common.Result;
import com.sf.common.StringConst;
import com.sf.entity.Files;
import com.sf.exception.ServiceException;
import com.sf.mapper.FilesMapper;
import com.sf.service.IFilesService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.utils.OBSUtils;
import com.sf.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import java.io.*;
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
    private static final Logger log = LoggerFactory.getLogger(FilesServiceImpl.class);

    @Value("${files.upload.path}")
    private String fileUploadPath;

    @Resource
    private FilesMapper fileMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

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

        try {
            // 将获取到的文件存储到磁盘目录
            file.transferTo(uploadFile);
        } catch (FileNotFoundException e) {
            log.warn("保存文件出错");
            throw new ServiceException(Constants.CODE_600, "系统错误，请联系管理员");
        }

        //获取文件md5
        String fileMD5 = SecureUtil.md5(uploadFile);


        //根据md5查询是否有重复文件
        String urlFromSql = selectFileByMD5(fileMD5);

        // 存储数据库的file
        Files saveFile = new Files();

        //从数据库查不到相同的md5，则上传，否则将已有文件的url其该文件最终url
        if (urlFromSql != null) {
            saveFile.setUrl(urlFromSql);
            //相同md5的文件存在，删除已保存文件
            uploadFile.delete();
            finalUrl = urlFromSql;

        } else {
            //无相同md5文件，则上传
            try {
                PutObjectResult result = OBSUtils.uploadFile(fileUUID, uploadFile.getPath());
                assert result != null;
                finalUrl = result.getObjectUrl();
                saveFile.setUrl(finalUrl);
            } catch (Exception e) {
                log.warn("上传文件失败");
                throw new ServiceException(Constants.CODE_600, "上传失败，请重试");
            }

        }

        saveFile.setName(originalFilename);
        saveFile.setType(type);
        saveFile.setSize(size / 1024);
        saveFile.setMd5(fileMD5);

        fileMapper.insert(saveFile);

        //存储完后删除文件
        uploadFile.delete();


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
     * 查找所有文件
     *
     * @return
     */
    @Override
    public List<Files> findAllFile() {
        //先从缓冲获取数据
        String jsonStr = stringRedisTemplate.opsForValue().get(StringConst.FILE_KEY);

        List<Files> files;
        if (StrUtil.isBlank(jsonStr)) {
            //没有查询到json
            //从数据库取出数据
            files = fileMapper.selectList(null);
            //将数据缓存到redis
            stringRedisTemplate.opsForValue().set(StringConst.FILE_KEY, JSONUtil.toJsonStr(files));
        } else {
            //从redis中获取数据
            files = JSONUtil.toBean(jsonStr, new TypeReference<List<Files>>() {
            }, true);
        }


        return files;
    }

    /**
     * 下载单文件
     *
     * @param fileId
     * @param outputStream
     * @return
     */
    @Override
    public byte[] downloadFile(Integer fileId, ServletOutputStream outputStream) {

        byte[] bytes = new byte[0];
        try {
            //通过文件路径读取文件字节流

            Files files = fileMapper.selectById(fileId);
            String[] split = files.getUrl().split("https://speakfreely.obs.cn-north-4.myhuaweicloud.com:443/");
            if (split.length != 2) {
                ByteArrayOutputStream byteArrayOutputStream = OBSUtils.downloadFile(split[1]);
                bytes = byteArrayOutputStream.toByteArray();
                return bytes;
            } else {
                throw new ServiceException(Constants.CODE_600, "文件url出错");
            }

        } catch (IOException e) {
            log.warn("没有找到文件");
            throw new ServiceException(Constants.CODE_600, "下载文件出错");
        }

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

    /**
     * 根据文件md5查询文件
     *
     * @param md5
     * @return
     */
    private String selectFileByMD5(String md5) {
        QueryWrapper<Files> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("md5", md5);
        List<Files> filesList = fileMapper.selectList(queryWrapper);
        return filesList.size() == 0 ? null : filesList.get(0).getUrl();


    }
}
