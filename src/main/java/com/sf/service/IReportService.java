package com.sf.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.sf.entity.Comment;
import com.sf.entity.Report;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.entity.dto.ReportDto;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leung
 * @since 2022-06-04
 */
public interface IReportService extends IService<Report> {

    /**
     * 保存举报
     * @param report
     */
    void saveReport(Report report);

    /**
     * 修改举报
     * @param report
     */
    void updateReport(Report report);

    /**
     * 分页查找举报
     * @param pages
     * @param pageSize
     * @param reportDto
     * @return
     */
    IPage<Report> getPage(Integer pageNum, Integer pageSize, ReportDto reportDto);
}
