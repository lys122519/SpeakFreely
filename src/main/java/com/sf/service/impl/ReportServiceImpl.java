package com.sf.service.impl;

import com.sf.entity.Report;
import com.sf.mapper.ReportMapper;
import com.sf.service.IReportService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leung
 * @since 2022-06-04
 */
@Service
public class ReportServiceImpl extends ServiceImpl<ReportMapper, Report> implements IReportService {

}
