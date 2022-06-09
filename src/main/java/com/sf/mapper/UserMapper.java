package com.sf.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.sf.entity.User;
import com.sf.entity.dto.DataDto;
import com.sf.entity.dto.FileDataDto;
import com.sf.entity.dto.UserDataDto;
import org.apache.ibatis.annotations.Mapper;

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


    /**
     * 根据性别信息分类查找用户数
     * @return
     */
    List<UserDataDto> selectUserSexCount();
}
