package com.sf.service;

import com.sf.entity.ActiveUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.ArrayList;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leung
 * @since 2022-06-06
 */
public interface IActiveUserService extends IService<ActiveUser> {

    /**
     * 按小时 查找活跃用户数
     *
     * @return 开始时间往前偏移24小时内的所有数据
     */
    ArrayList<Integer> findActiveUserCountByHour(String startTime);
}
