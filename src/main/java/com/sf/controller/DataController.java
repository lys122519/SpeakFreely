package com.sf.controller;

import cn.hutool.json.JSONObject;
import com.sf.common.Result;
import com.sf.common.StringConst;
import com.sf.entity.dto.InterfaceDto;
import com.sf.service.impl.ActiveUserServiceImpl;
import com.sf.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Description:
 * @author: leung
 * @date: 2022-06-05 19:36
 */

@RestController
@RequestMapping("/data")
@Api(tags = "数据统计相关接口")
public class DataController {

    private static final Logger log = LoggerFactory.getLogger(DataController.class);

    @Resource
    private ActiveUserServiceImpl activeUserService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @GetMapping("/interface/{intCount}")
    @ApiOperation(value = "查找接口成功访问次数（默认倒序）")
    public Result<List<InterfaceDto>> findInterfaceCount(
            @ApiParam(name = "intCount", value = "需要的接口数") @PathVariable Integer intCount
    ) {

        HashMap<String, Integer> hashMap = new HashMap<>();
        JSONObject jsonObject = RedisUtils.objFromRedis(StringConst.INTERFACE_ACTUATOR);

        if (jsonObject != null) {
            Set<String> strings = jsonObject.keySet();
            for (String string : strings) {
                Integer o = (Integer) jsonObject.get(string);
                hashMap.putIfAbsent(string, o);
            }
        }

        //根据value倒序
        Map<String, Integer> sortedMap = hashMap.entrySet().stream().sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));

        //返回列表
        List<InterfaceDto> resultList = new ArrayList<>();

        //排序后中的map中所有的key
        Object[] objects = sortedMap.keySet().toArray();
        for (int i = 0; i < intCount; i++) {
            InterfaceDto interfaceDto = new InterfaceDto();
            interfaceDto.setName((String) objects[i]);
            interfaceDto.setCount(sortedMap.get((String) objects[i]));
            resultList.add(interfaceDto);
        }

        return Result.success(resultList);
    }

    @GetMapping("/activeUserCountByHour")
    @ApiOperation(value = "查询系统活跃用户数(24小时内)", notes = ",整点统计")
    public Result<ArrayList<Integer>> findActiveUserCount() {

        ArrayList<Integer> activeList = new ArrayList<>();

        int size = Objects.requireNonNull(stringRedisTemplate.opsForList().size(StringConst.ACTIVE_USER)).intValue();
        //取size个数据
        List<String> range = stringRedisTemplate.opsForList().range(StringConst.ACTIVE_USER, 0, size);
        assert range != null;
        //将redis中对应结果存入list
        for (String element : range) {
            activeList.add(Integer.valueOf(element));
        }

        return Result.success(activeList);
    }
}
