package com.sf.service;

import cn.hutool.json.JSONObject;
import com.sf.entity.Tags;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author leung
 * @since 2022-06-02
 */
public interface ITagsService extends IService<Tags> {

    JSONObject getTop100(); // 返回热度前100的标签列表
}
