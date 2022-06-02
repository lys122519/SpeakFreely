package com.sf.service;

import com.sf.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.sf.entity.dto.UserDTO;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author leung
 * @since 2022-05-30
 */
public interface IUserService extends IService<User> {
    void sendEmailCode(String action, String email); // 邮箱验证码发送接口

    void userPwdReset(String email, String newPwd, String code);// 用户密码重置接口

    UserDTO userLogin(String username, String password); // 用户登录接口(用户名或邮箱均由username传递)

    UserDTO userRegister(UserDTO userDTO); // 用户注册接口

    UserDTO userInfoModify(UserDTO userDTO); // 用户信息修改接口

    UserDTO emailModify(UserDTO userDTO); // 用户邮箱换绑接口
}
