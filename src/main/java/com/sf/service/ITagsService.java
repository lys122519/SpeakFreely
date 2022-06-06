package com.sf.service;

import cn.hutool.json.JSONObject;
import com.sf.entity.Tags;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leung
 * @since 2022-06-02
 */
public interface ITagsService extends IService<Tags> {

    List<JSONObject> getTop100(); // 返回热度前100的标签列表
}
