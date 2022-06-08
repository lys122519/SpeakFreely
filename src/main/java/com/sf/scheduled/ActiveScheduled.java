package com.sf.scheduled;

import cn.hutool.core.date.DateUtil;
import com.sf.common.StringConst;
import com.sf.service.impl.ActiveUserServiceImpl;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * @Description: 定时任务
 * @author: leung
 * @date: 2022-06-05 22:47
 */
@Component
public class ActiveScheduled {

    @Resource
    private ActiveUserServiceImpl activeUserService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Scheduled(cron = "0 0 0-23 * * ?")
    //@Scheduled(cron = "0/5 * * * * ?")
    public void countActUser() {

        String nowTime = DateUtil.now();
        String countTime = nowTime.substring(0, 14) + "00:00";
        //ArrayList<Integer> activeUserCountByHour = activeUserService.findActiveUserCountByHour(nowTime);
        Integer activeUserCount = activeUserService.findActiveUserByHour(countTime);

        if (Objects.requireNonNull(stringRedisTemplate.opsForList().size(StringConst.ACTIVE_USER)).intValue() < 24) {
            stringRedisTemplate.opsForList().rightPush(StringConst.ACTIVE_USER, String.valueOf(activeUserCount));
        } else {
            //右进左出
            stringRedisTemplate.opsForList().rightPush(StringConst.ACTIVE_USER, String.valueOf(activeUserCount));
            stringRedisTemplate.opsForList().leftPop(StringConst.ACTIVE_USER);
        }

    }
}
