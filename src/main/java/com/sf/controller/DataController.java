package com.sf.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.sf.actuator.SysData;
import com.sf.common.Result;
import com.sf.common.StringConst;
import com.sf.entity.Article;
import com.sf.entity.Comment;
import com.sf.entity.User;
import com.sf.entity.dto.*;
import com.sf.mapper.ArticleMapper;
import com.sf.mapper.CommentMapper;
import com.sf.mapper.FilesMapper;
import com.sf.mapper.UserMapper;
import com.sf.utils.RedisUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
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
    private UserMapper userMapper;

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private CommentMapper commentMapper;


    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private FilesMapper filesMapper;

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

    @GetMapping("/fileCount/{intCount}")
    @ApiOperation(value = "查询系统文件数", notes = "分类")
    public Result<ArrayList<FileDataDto>> findFileCount(
            @ApiParam(name = "intCount", value = "需要的类型数") @PathVariable Integer intCount

    ) {
        ArrayList<FileDataDto> fileDataDtos = (ArrayList<FileDataDto>) filesMapper.selectFileCount(intCount);
        return Result.success(fileDataDtos);
    }


    @GetMapping("/sysInfo")
    @ApiOperation(value = "获得系统重要信息")
    public Result<SysDto> findSysInfo() {
        SysDto sysInfo = SysData.getSysInfo();
        return Result.success(sysInfo);
    }

    @GetMapping("/userCount")
    @ApiOperation(value = "获得系统用户数与活跃用户")
    public Result<List<DataDto>> findUserCount() {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Long aLong = userMapper.selectCount(queryWrapper);
        //结果数组
        List<DataDto> resultList = new ArrayList<>();

        DataDto allUserCount = new DataDto();

        allUserCount.setName("userCount");
        allUserCount.setCount(Math.toIntExact(aLong));

        //获得redis所有的key
        Set<String> keys = stringRedisTemplate.keys("*");

        DataDto onlineUser = new DataDto();
        onlineUser.setName("activeUser");
        if (keys != null && keys.size() != 0) {
            int count = 0;
            for (String key : keys) {
                String[] split = key.split("\\.");
                if (split.length == 3) {
                    count++;
                }
            }
            onlineUser.setCount(count);
        } else {
            onlineUser.setCount(0);
        }

        //加入系统用户数
        resultList.add(allUserCount);
        resultList.add(onlineUser);
        return Result.success(resultList);
    }


    @GetMapping("/sexCount")
    @ApiOperation(value = "用户性别统计")
    public Result<List<UserDataDto>> findUserSexCount() {

        List<UserDataDto> resultList = userMapper.selectUserSexCount();

        return Result.success(resultList);
    }

    @GetMapping("/articleAndCommentCount")
    @ApiOperation(value = "文章和评论数量统计")
    public Result<List<DataDto>> findArticleCount() {
        QueryWrapper<Article> queryWrapper  =new QueryWrapper<>();

        Long articleCount = articleMapper.selectCount(queryWrapper);


        List<DataDto> resultList = new ArrayList<>();

        DataDto articleDto = new DataDto();
        articleDto.setName("article");
        articleDto.setCount(Math.toIntExact(articleCount));
        resultList.add(articleDto);


        QueryWrapper<Comment> commentQueryWrapper =new QueryWrapper<>();
        Long commentCount = commentMapper.selectCount(commentQueryWrapper);

        DataDto commentDto = new DataDto();
        commentDto.setName("comment");
        commentDto.setCount(Math.toIntExact(commentCount));
        resultList.add(commentDto);

        return Result.success(resultList);
    }

}
