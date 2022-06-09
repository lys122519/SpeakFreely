package com.sf.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.entity.ActiveUser;
import com.sf.mapper.ActiveUserMapper;
import com.sf.service.IActiveUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leung
 * @since 2022-06-06
 */
@Transactional(rollbackFor = Exception.class)
@Service
public class ActiveUserServiceImpl extends ServiceImpl<ActiveUserMapper, ActiveUser> implements IActiveUserService {

    @Resource
    private ActiveUserMapper activeUserMapper;


    public Integer findActiveUserByHour(String startTime) {

        //按小时查找
        DateTime dateTime = DateUtil.parse(startTime);

        String startTime1 = DateUtil.offsetHour(dateTime, 0).toString();
        String endTime1 = DateUtil.offsetHour(dateTime, -1).toString();
        QueryWrapper<ActiveUser> queryWrapper = new QueryWrapper<>();

        queryWrapper.select("DISTINCT user_id");
        queryWrapper.between("time", endTime1, startTime1);

        Long count = activeUserMapper.selectCount(queryWrapper);

        return Math.toIntExact(count);

    }


    @Override
    public ArrayList<Integer> findActiveUserCountByHour(String startTime) {
        ArrayList<Integer> resultList = new ArrayList<>();

        //按小时查找
        DateTime dateTime = DateUtil.parse(startTime);

        for (int i = 0; i < 24; i++) {
            String startTime1 = DateUtil.offsetHour(dateTime, -i).toString();
            String endTime1 = DateUtil.offsetHour(dateTime, 1 - i).toString();
            QueryWrapper<ActiveUser> queryWrapper = new QueryWrapper<>();

            queryWrapper.select("DISTINCT user_id");
            queryWrapper.between("time", startTime1, endTime1);

            Long count = activeUserMapper.selectCount(queryWrapper);

            resultList.add(Math.toIntExact(count));
        }


        //数组逆序 从24小时前数据开始排序
        Collections.reverse(resultList);

        return resultList;

    }
}
