package com.sf.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Constants;
import com.sf.common.Result;
import com.sf.common.StringConst;
import com.sf.entity.dto.UserDTO;
import com.sf.exception.ServiceException;
import com.sf.utils.TokenUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


import com.sf.service.IArticleService;
import com.sf.entity.Article;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
@RestController
@RequestMapping("/article")
@Api(tags = "文章相关接口")
public class ArticleController {
    private static final Logger log = LoggerFactory.getLogger(ArticleController.class);

    @Autowired
    private IArticleService articleService;

    @DeleteMapping("/{id}")
    @ApiOperation(value = "根据id删除")
    public Result<Void> delete(@PathVariable Integer id) {
        articleService.removeById(id);
        return Result.success();
    }

    @PostMapping("/del/batch")
    @ApiOperation(value = "批量删除")
    public Result<Void> deleteBatch(@RequestBody List<Integer> ids) {
        articleService.removeBatchByIds(ids);
        return Result.success();
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "根据id查找一个")
    public Result<Article> findOne(@PathVariable Integer id) {
        return Result.success(articleService.getById(id));
    }

    @GetMapping
    @ApiOperation(value = "查找所有")
    public Result<List<Article>> findAll() {
        return Result.success(articleService.list());
    }

    @GetMapping("/page")
    @ApiOperation(value = "分页查找")
    public Result<IPage<Article>> findPage(@RequestParam Integer pageNum,
                                           @RequestParam Integer pageSize,
                                           @RequestParam(defaultValue = "") String name) {

        Page<Article> page = articleService.findPage(new Page<>(pageNum, pageSize), name);
        return Result.success(page);
    }

    @ApiOperation(value = "文章(旧文章和旧草稿需id) 存草稿/文章发布", notes = "必须参数：action:draft(新草稿保存/旧草稿更新/旧文章存草稿)/publish(新文章发布/旧文章更新/旧草稿发布);Article(id或content至少有一项)", httpMethod = "POST")
    @PostMapping("/action/{action}")
    public Result<Article> articleAction(@PathVariable String action, @RequestBody Article article) {
        if ((action.equals(StringConst.ARTICLE_DRAFT) || action.equals(StringConst.ARTICLE_PUBLISH) && StrUtil.isNotBlank(article.getContent())) && StrUtil.isNotBlank(article.getName())) {
            return Result.success(articleService.articleAction(action, article));
        } else {
            throw new ServiceException(Constants.CODE_400, "参数异常!");
        }
    }
}
