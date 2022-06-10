package com.sf.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Constants;
import com.sf.common.Result;
import com.sf.common.StringConst;
import com.sf.config.AuthAccess;
import com.sf.entity.dto.ArticleDTO;
import com.sf.exception.ServiceException;
import com.sf.mapper.ArticleMapper;
import com.sf.mapper.TagsArticleMapper;
import com.sf.mapper.TagsMapper;
import com.sf.utils.RedisUtils;
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

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private TagsMapper tagsMapper;

    @Autowired
    private TagsArticleMapper tagsArticleMapper;

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

    @AuthAccess
    @GetMapping("/{id}")
    @ApiOperation(value = "根据文章id获取文章详细信息(包含文章内容，作者和标签信息)", notes = "用户已登录,请求路径包含文章id", httpMethod = "GET")
    public Result<JSONObject> findOne(@PathVariable Integer id) {
        JSONObject article = new JSONObject(articleMapper.getArticleByID(id)); // 获取文章信息(包含作者信息)
        if (article.size() > 0) {// 有已发布文章才给标签
            article.set("tags", tagsArticleMapper.getTagsByArticle(id)); // 获取标签列表
        }
        return Result.success(article);
    }

    @GetMapping("/self/{type}/{page}/{limit}")
    @ApiOperation(value = "作者自身获取未被删除文章列表(不包含文章内容，包含作者和标签信息)", notes = "token(headers),type(draft草稿/publish已发布/all所有),page(页数),limit(每页限制)", httpMethod = "GET")
    public Result<IPage<ArticleDTO>> getSelfArticle(@PathVariable String type, @PathVariable Integer page, @PathVariable Integer limit) {
        // 作者获取自身文章列表(包含作者信息)
        Integer id = RedisUtils.getCurrentUserId(TokenUtils.getToken());// 根据token获取作者id
        return Result.success(articleService.pageArticle(new Page<>(page, limit), id, type, null));
    }

    @AuthAccess
    @GetMapping("/author/{id}/{type}/{page}/{limit}")
    @ApiOperation(value = "指定作者id获取文章列表(不包含文章内容，包含作者和标签信息)", notes = "用户已登录,请求路径包含文章作者id,type(draft草稿/publish已发布/all所有),page(页数),limit(每页限制)", httpMethod = "GET")
    public Result<IPage<ArticleDTO>> getAuthorArticle(@PathVariable Integer id, @PathVariable String type, @PathVariable Integer page, @PathVariable Integer limit) {
        return Result.success(articleService.pageArticle(new Page<>(page, limit), id, type, null));
    }

    @GetMapping("/{type}/{page}/{limit}")
    @ApiOperation(value = "分页获取所有作者文章列表(不包含文章内容，包含作者和标签)", notes = "用户已登录,searchArticleTitle(要获取的文章标题),type(draft草稿/publish已发布/all所有),page(页数),limit(每页限制)", httpMethod = "GET")
    public Result<IPage<ArticleDTO>> getArticleList(ArticleDTO articleDTO, @PathVariable String type, @PathVariable Integer page, @PathVariable Integer limit) {
        return Result.success(articleService.pageArticle(new Page<>(page, limit), null, type, articleDTO.getSearchArticleTitle()));
    }

    @AuthAccess
    @GetMapping("/search/{page}/{limit}")
    @ApiOperation(value = "根据标签id和文章标题(至少一个不为空)分页搜索文章列表(不包含文章内容)", notes = "用户已登录,请求体中(searchTagID(标签ID),searchArticleTitle(文章标题)),page(页数),limit(每页限制)", httpMethod = "GET")
    public Result<IPage<ArticleDTO>> pageSearchArticle(@PathVariable Integer page, @PathVariable Integer limit, ArticleDTO articleDTO) {
        if (articleDTO.getSearchTagID() != null || StrUtil.isNotBlank(articleDTO.getSearchArticleTitle())) {
            return Result.success(articleMapper.pageArticle(new Page<>(page, limit), null, 1, articleDTO.getSearchTagID(), articleDTO.getSearchArticleTitle()));
        } else {
            throw new ServiceException(Constants.CODE_400, "标签ID和文章标题不能全为空!");
        }
    }

    @GetMapping
    @ApiOperation(value = "查找所有")
    public Result<List<Article>> findAll() {
        return Result.success(articleService.list());
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

    @AuthAccess
    @PostMapping("/addCounts/{articleID}")
    @ApiOperation(value = "增加访问量", notes = "文章id", httpMethod = "POST")
    public Result<Void> addCounts(@PathVariable Integer articleID) {
        articleService.addCounts(articleID);
        return Result.success();
    }

    @AuthAccess
    @GetMapping("/top/{page}/{limit}")
    @ApiOperation(value = "返回文章热度排序结果列表", notes = "page(页数),limit(每页限制)", httpMethod = "GET")
    public Result<Page<ArticleDTO>> pageTopArticle(@PathVariable Integer page, @PathVariable Integer limit) {
        return Result.success(articleMapper.pageTopArticle(new Page<>(page, limit)));
    }
}
