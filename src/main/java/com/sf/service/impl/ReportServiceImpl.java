package com.sf.service.impl;

import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.entity.Report;
import com.sf.entity.dto.ReportDto;
import com.sf.mapper.ReportMapper;
import com.sf.service.IReportService;
import com.sf.utils.RedisUtils;
import com.sf.utils.TokenUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leung
 * @since 2022-06-04
 */
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements IReportService {

    @Resource
    private ReportMapper reportMapper;

    /**
     * 保存举报
     *
     * @param report
     */
    @Override
    public void saveReport(Report report) {
        report.setTime(DateUtil.now());
        report.setUserId(RedisUtils.getCurrentUserId(TokenUtils.getToken()));

        reportMapper.insert(report);
    }

    /**
     * 修改举报
     *
     * @param report
     */
    @Override
    public void updateReport(Report report) {
        reportMapper.updateById(report);
    }

    /**
     * 分页查找举报
     *
     * @param pageNum
     * @param pageSize
     * @param report
     * @return
     */
    @Override
    public IPage<ReportDto> getPage(Integer pageNum, Integer pageSize, ReportDto reportDto) {

        IPage<ReportDto> page = new Page<>(pageNum, pageSize);
        reportMapper.findPage(page, reportDto);
        return page;

    }
}
