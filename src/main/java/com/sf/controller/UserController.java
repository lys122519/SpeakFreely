package com.sf.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import com.sf.service.IUserService;
import com.sf.entity.User;

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

    @DeleteMapping("/{id}")
    public Result delete(@PathVariable Integer id) {
        return Result.success(userService.removeById(id));
    }

    @PostMapping("/del/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        return Result.success(userService.removeBatchByIds(ids));
    }

    @GetMapping("/{id}")
    public Result findOne(@PathVariable Integer id) {
        return Result.success(userService.getById(id));
    }

    @GetMapping
    public Result findAll() {
        return Result.success(userService.list());
    }

    @GetMapping("/page")
    public Result findPage(@RequestParam Integer pageNum,
                           @RequestParam Integer pageSize) {
        return Result.success(userService.page(new Page<>(pageNum, pageSize)));
    }
}
