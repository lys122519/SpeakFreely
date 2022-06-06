package com.sf.mapper;

import cn.hutool.json.JSONObject;
import com.sf.entity.Tags;
import com.sf.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.lang.reflect.Field;
import java.util.List;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author leung
 * @since 2022-05-30
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
