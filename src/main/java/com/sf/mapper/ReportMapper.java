package com.sf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.entity.Report;
import com.sf.entity.dto.ReportDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leung
 * @since 2022-06-04
 */
@Mapper
public interface ReportMapper extends BaseMapper<Report> {

    /**
     * 分页查找
     *
     * @param page
     * @param report
     * @return
     */
    IPage<ReportDto> findPage(IPage<ReportDto> page, @Param("report") ReportDto reportDto);
}
