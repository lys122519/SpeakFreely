package com.sf.mapper;

import com.sf.entity.Tags;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author leung
 * @since 2022-06-02
 */
@Mapper
public interface TagsMapper extends BaseMapper<Tags> {
    /**
     * 批量插入tag数据
     * 除了内置标签外，插入的均为用户自建标签，所以插入时即代表已被引用，热度默认为1
     */
    void batchAddList(@Param("samples") List<Tags> samples);
}
