package com.sf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.entity.Files;
import com.sf.entity.dto.FileDataDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leung
 * @since 2022-05-30
 */
@Mapper
public interface FilesMapper extends BaseMapper<Files> {

    /**
     * 按类型查找文件数
     * @return
     * @param intCount
     */
    List<FileDataDto> selectFileCount(@Param("count") Integer intCount);

}
