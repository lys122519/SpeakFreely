package com.sf.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Result;
import com.sf.config.AuthAccess;
import com.sf.exception.ServiceException;
import com.sf.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import com.sf.service.IUserService;
import com.sf.entity.User;
import com.sf.entity.dto.UserDTO;
import com.sf.common.Constants;

import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author leung
 * @since 2022-05-30
 */

@RestController
@RequestMapping("/user")
@Api(tags = "用户相关接口")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Resource
    private IUserService userService;

    @ApiOperation(value = "新增/修改接口")

    @PostMapping
    public Result<Void> save(@RequestBody User user) {
        userService.saveOrUpdate(user);
        return Result.success();
    }

    @ApiOperation(value = "根据id删除")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Integer id) {
        userService.removeById(id);
        return Result.success();
    }

    @ApiOperation(value = "批量删除")
    @PostMapping("/del/batch")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        userService.removeBatchByIds(ids);
        return Result.success();
    }

    @ApiOperation(value = "根据id查找一个")
    @GetMapping("/{id}")
    public Result<User> findOne(@PathVariable Integer id) {
        return Result.success(userService.getById(id));
    }

    @ApiOperation(value = "查找所有")
    @GetMapping
    public Result<List<User>> findAll() {
        return Result.success(userService.list());
    }

    @ApiOperation(value = "分页查找")
    @GetMapping("/page")
    public Result<IPage<User>> findPage(@RequestParam Integer pageNum,
                                        @RequestParam Integer pageSize) {
        return Result.success(userService.page(new Page<>(pageNum, pageSize)));
    }

    @ApiOperation(value = "用户登录", notes = "必须参数：username/email + password", httpMethod = "POST")
    @PostMapping("/login")
    public Result<UserDTO> userLogin(@RequestBody UserDTO userDTO) {
        if (StrUtil.isNotBlank(userDTO.getUsername()) && StrUtil.isNotBlank(userDTO.getPassword())) {
            return Result.success(userService.userLogin(userDTO.getUsername(), userDTO.getPassword()));
        } else {
            throw new ServiceException(Constants.CODE_400, "参数异常!");
        }
    }

    @ApiOperation(value = "用户忘记密码", notes = "必须参数：email+password+code", httpMethod = "POST")
    @PostMapping("/pwdReset")
    public Result<String> userPwdReset(@RequestBody UserDTO userDTO) {
        if (StrUtil.isNotBlank(userDTO.getEmail()) && StrUtil.isNotBlank(userDTO.getPassword()) &&
                StrUtil.isNotBlank(userDTO.getCode())) {
            userService.userPwdReset(userDTO.getEmail(), userDTO.getPassword(), userDTO.getCode());
            return Result.success("密码重置成功");
        } else {
            throw new ServiceException(Constants.CODE_400, "参数异常!");
        }
    }

    @ApiOperation(value = "用户信息修改", notes = "必须参数：token+code((nickname/phone/address/avatarUrl)修改不需要)", httpMethod = "POST")
    @PostMapping("/infoModify")
    public Result<UserDTO> userInfoModify(@RequestBody UserDTO userDTO) {
        userDTO.setToken(TokenUtils.getToken());
        if (StrUtil.isNotBlank(userDTO.getToken())) {
            return Result.success(userService.userInfoModify(userDTO));
        } else {
            throw new ServiceException(Constants.CODE_400, "参数异常!");
        }
    }

    @ApiOperation(value = "用户注册", notes = "必须参数：username+email+password+code", httpMethod = "POST")
    @PostMapping("/register")
    public Result<UserDTO> userRegister(@RequestBody UserDTO userDTO) {
        if (StrUtil.isNotBlank(userDTO.getUsername()) && StrUtil.isNotBlank(userDTO.getEmail()) &&
                StrUtil.isNotBlank(userDTO.getPassword()) && StrUtil.isNotBlank(userDTO.getCode())) {
            return Result.success(userService.userRegister(userDTO));
        } else {
            throw new ServiceException(Constants.CODE_400, "参数异常!");
        }
    }

    @ApiOperation(value = "邮箱换绑", notes = "必须参数：token+email+code", httpMethod = "POST")
    @PostMapping("/emailModify")
    public Result<UserDTO> emailModify(@RequestBody UserDTO userDTO) {
        userDTO.setToken(TokenUtils.getToken());
        if (StrUtil.isNotBlank(userDTO.getToken()) && StrUtil.isNotBlank(userDTO.getEmail()) &&
                StrUtil.isNotBlank(userDTO.getCode())) {
            return Result.success(userService.emailModify(userDTO));
        } else {
            throw new ServiceException(Constants.CODE_400, "参数异常!");
        }
    }

    @AuthAccess
    @ApiOperation(value = "发送邮箱验证码", notes = "action:emailRegister(注册邮件)/emailPwdReset(忘记密码)/emailInfoModify(信息修改)/emailModify(邮箱换绑);email为目标邮箱", httpMethod = "POST")
    @PostMapping("/emailCode/{action}/{email}")
    public Result<String> sendEmailCode(@PathVariable String action, @PathVariable String email) {
        if (StrUtil.isNotBlank(action) && StrUtil.isNotBlank(email)) { // 检验参数是否均不为空
            userService.sendEmailCode(action, email); // 调用邮箱验证码发送接口发送验证码
            return Result.success("验证码发送成功");
        } else { // 参数异常处理
            throw new ServiceException(Constants.CODE_400, "参数异常!");
        }
    }
}
