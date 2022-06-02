package com.sf.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Result;
import com.sf.config.AuthAccess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
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

    @Resource
    private IUserService userService;

    @ApiOperation(value = "新增/修改接口")

    @PostMapping
    public Result save(@RequestBody User user) {
        return Result.success(userService.saveOrUpdate(user));
    }

    @ApiOperation(value = "根据id删除")
    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(userService.removeById(id));
    }

    @ApiOperation(value = "批量删除")
    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(userService.removeBatchByIds(ids));
    }

    @ApiOperation(value = "根据id查找一个")
    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(userService.getById(id));
    }

    @ApiOperation(value = "查找所有")
    @GetMapping
    public Result findAll() {
        return Result.success(userService.list());
    }

    @ApiOperation(value = "分页查找")
    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        return Result.success(userService.page(new Page<>(pageNum, pageSize)));
    }

    @ApiOperation(value = "用户登录", notes = "必须参数：username/email + password", httpMethod = "POST")
    @PostMapping("/login")
    public Result userLogin(@RequestBody UserDTO userDTO) {
        if (StrUtil.isNotBlank(userDTO.getUsername()) && StrUtil.isNotBlank(userDTO.getPassword())) {
            return Result.success(userService.userLogin(userDTO.getUsername(), userDTO.getPassword()));
        } else {
            return Result.error(Constants.CODE_400, "参数异常!");
        }
    }

    @ApiOperation(value = "用户忘记密码", notes = "必须参数：email+password+code", httpMethod = "POST")
    @PostMapping("/pwdReset")
    public Result userPwdReset(@RequestBody UserDTO userDTO) {
        if (StrUtil.isNotBlank(userDTO.getEmail()) && StrUtil.isNotBlank(userDTO.getPassword()) &&
                StrUtil.isNotBlank(userDTO.getCode())) {
            userService.userPwdReset(userDTO.getEmail(), userDTO.getPassword(), userDTO.getCode());
            return Result.success("密码重置成功");
        } else {
            return Result.error(Constants.CODE_400, "参数异常!");
        }
    }

    @ApiOperation(value = "用户信息修改", notes = "必须参数：token+code", httpMethod = "POST")
    @PostMapping("/infoModify")
    public Result userInfoModify(@RequestBody UserDTO userDTO) {
        if (StrUtil.isNotBlank(userDTO.getToken()) && StrUtil.isNotBlank(userDTO.getCode())) {
            return Result.success(userService.userInfoModify(userDTO));
        } else {
            return Result.error(Constants.CODE_400, "参数异常!");
        }
    }

    @ApiOperation(value = "用户注册", notes = "必须参数：username+email+password+code", httpMethod = "POST")
    @PostMapping("/register")
    public Result userRegister(@RequestBody UserDTO userDTO) {
        if (StrUtil.isNotBlank(userDTO.getUsername()) && StrUtil.isNotBlank(userDTO.getEmail()) &&
                StrUtil.isNotBlank(userDTO.getPassword()) && StrUtil.isNotBlank(userDTO.getCode())) {
            return Result.success(userService.userRegister(userDTO));
        } else {
            return Result.error(Constants.CODE_400, "参数异常!");
        }
    }

    @AuthAccess
    @ApiOperation(value = "发送邮箱验证码", notes = "action:emailRegister(注册邮件)/emailPwdReset(忘记密码)/emailInfoModify(信息修改);email为目标邮箱", httpMethod = "POST")
    @PostMapping("/emailCode/{action}/{email}")
    public Result sendEmailCode(@PathVariable String action, @PathVariable String email) {
        if (StrUtil.isNotBlank(action) && StrUtil.isNotBlank(email)) { // 检验参数是否均不为空
            userService.sendEmailCode(action, email); // 调用邮箱验证码发送接口发送验证码
            return Result.success("验证码发送成功");
        } else { // 参数异常处理
            return Result.error(Constants.CODE_400, "参数异常!");
        }
    }
}
