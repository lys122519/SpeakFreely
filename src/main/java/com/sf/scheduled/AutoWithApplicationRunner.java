package com.sf.scheduled;

import cn.hutool.core.date.DateUtil;
import com.sf.common.StringConst;
import com.sf.service.impl.ActiveUserServiceImpl;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Objects;

/**
 * @Description: 启动时如果redis为空初始化 活跃用户key
 * @author: leung
 * @date: 2022-06-06 17:42
 */
@Component
public class AutoWithApplicationRunner implements ApplicationRunner {

    @Resource
    private ActiveUserServiceImpl activeUserService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        int size = Objects.requireNonNull(stringRedisTemplate.opsForList().size(StringConst.ACTIVE_USER)).intValue();
        if (size == 0) {
            String nowTime = DateUtil.now();
            String countTime = nowTime.substring(0, 14) + "00:00";
            ArrayList<Integer> activeUserCountByHour = activeUserService.findActiveUserCountByHour(countTime);
            for (Integer integer : activeUserCountByHour) {
                stringRedisTemplate.opsForList().rightPush(StringConst.ACTIVE_USER, String.valueOf(integer));
            }
        }
    }
}
