package com.sf.service.impl;

import com.sf.entity.Tags;
import com.sf.mapper.TagsMapper;
import com.sf.service.ITagsService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author leung
 * @since 2022-06-02
 */
@Service
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags> implements ITagsService {

}
