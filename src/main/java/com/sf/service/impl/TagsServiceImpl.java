package com.sf.service.impl;

import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.common.Constants;
import com.sf.common.StringConst;
import com.sf.entity.Tags;
import com.sf.mapper.TagsMapper;
import com.sf.service.ITagsService;
import com.sf.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leung
 * @since 2022-06-02
 */
@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags> implements ITagsService {
    private static final Logger log = LoggerFactory.getLogger(TagsServiceImpl.class);

    @Autowired
    TagsMapper tagsMapper;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    /*会先查询redis缓存，没查到再从mysql统计，然后存入redis(1小时)*/
    public List<JSONObject> getTop100() {
        // 先查询redis标签缓存
        Map<Object, Object> tagTop100 = RedisUtils.mapFromRedis(StringConst.TAGS_REDIS_KEY);
        if (tagTop100.size() == 0) { // 判断缓存是否为空
            QueryWrapper<Tags> queryWrapper = new QueryWrapper<>();
            queryWrapper.orderByDesc("counts");
            List<Tags> tagList = tagsMapper.selectList(queryWrapper); // 查询排序结果
            //按counts排序
            tagList.sort((o1, o2) -> {
                if (o1.getCounts() < o2.getCounts()) return 1;
                else return -1;
            });
            if (tagList.size() > 100) { // 超过100个标签则进行切片
                tagList = tagList.subList(0, 100);
            }
            for (Tags tags : tagList) { // 将标签对象(先变成json对象再转为字符串)压入tagTop100
                tagTop100.put(tags.getId().toString(), new JSONObject(tags).toString());
            }
            //将tagTop100放入redis(并设置过期时长)
            RedisUtils.mapToRedis(StringConst.TAGS_REDIS_KEY, tagTop100, Constants.TAG_REDIS_TIMEOUT);
        } else { // redis已存在标签map缓存信息
            // 将map还原为list进行排序
            List<JSONObject> articleList = RedisUtils.jsonListFromMap(tagTop100);
            tagTop100.clear();//清空map，重新排序
            //按counts排序
            articleList.sort((o1, o2) -> {
                if (Integer.parseInt(String.valueOf(o1.get("counts"))) < Integer.parseInt(String.valueOf(o2.get("counts"))))
                    return 1;
                else return -1;
            });
            // 将排序结果压入map
            for (JSONObject article : articleList) { // 将标签对象(先变成json对象再转为字符串)压入文章topMap
                tagTop100.put(article.get("id").toString(), new JSONObject(article).toString());
            }
        }
        return RedisUtils.jsonListFromMap(tagTop100);
    }
}
