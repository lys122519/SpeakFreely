package com.sf.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sf.common.Constants;
import com.sf.common.StringConst;
import com.sf.entity.Article;
import com.sf.entity.Tags;
import com.sf.entity.TagsArticle;
import com.sf.enums.ArticleEnum;
import com.sf.exception.ServiceException;
import com.sf.mapper.ArticleMapper;
import com.sf.mapper.TagsArticleMapper;
import com.sf.mapper.TagsMapper;
import com.sf.service.IArticleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sf.utils.ObjectActionUtils;
import com.sf.utils.RedisUtils;
import com.sf.utils.TokenUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author leung
 * @since 2022-05-31
 */
@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, Article> implements IArticleService {
    private static final Logger log = LoggerFactory.getLogger(ArticleServiceImpl.class);

    @Resource
    private ArticleMapper articleMapper;

    @Resource
    private TagsMapper tagsMapper;

    @Resource
    private TagsArticleMapper tagsArticleMapper;

    @Override
    public Page<Article> findPage(Page<Article> page, String name) {
        return articleMapper.findPage(page, name);
    }

    @Override
    public Article articleAction(String action, Article article) {
        // 设置文章的用户ID
        article.setUserId(RedisUtils.getCurrentUserId(TokenUtils.getToken()));
        // 设置文章的发布者username
        article.setUsername(RedisUtils.getCurrentUserAttr(TokenUtils.getToken(), "username"));
        // 1.没有文章ID必然没有用户ID：新文章发布/新草稿保存
        if (article.getId() == null) {
            setEnable(action, article); // 设置启用状态
            articleMapper.insert(article); // 向数据库插入新文章或新草稿信息
        } else { // 2.有文章ID必然有用户ID：旧草稿发布/旧文章更新/旧草稿更新/旧文章存草稿
            setEnable(action, article); // 设置启用状态
            articleMapper.updateById(article); // 向数据库更新文章或草稿信息
        }
        if (article.getTags() == null) {
            article.setTags(new ArrayList<>());
        }
        bindTags(article.getId(), article.getTags()); // 绑定标签操作
        return article;
    }

    /*设置是否启用文章*/
    public void setEnable(String action, Article article) {
        if (action.equals(StringConst.ARTICLE_PUBLISH)) { // 操作为文章发布
            article.setEnabled(ArticleEnum.ENABLE);// 设置文章状态为启用
        } else if (action.equals(StringConst.ARTICLE_DRAFT)) { // 操作为草稿保存
            article.setEnabled(ArticleEnum.DISABLE);// 设置文章状态为不启用
        } else {
            throw new ServiceException(Constants.CODE_400, "未知操作");
        }
    }

    /*设置文章与标签的绑定关系*/
    public void bindTags(Integer articleID, List<Tags> tagsList) {
        List<Integer> nowList = new ArrayList<>(); // 存放当前文章应绑定标签id的列表
        List<Integer> oldList = new ArrayList<>(); // 存放当前文章旧标签id的列表
        if (tagsList.size() > 0) {
            tagsList = newTagDeal(tagsList); // 先处理用户自建标签(处理过程会对tagsList进行更新)
        }
        for (Tags tag : tagsList) { // 根据处理后的tagsList构建nowList
            nowList.add(tag.getId());
        }
        for (TagsArticle tagsArticle : getOwnTag(articleID)) { // 根据查询结果构建oldList
            oldList.add(tagsArticle.getTagId());
        }
//      log.warn(nowList.toString());
//      log.warn(oldList.toString());
        // 通过差集确定要删除的标签id列表和要新增的标签id列表
        List<Integer> deleteList = ObjectActionUtils.listComplement(oldList, nowList);
        List<Integer> addList = ObjectActionUtils.listComplement(nowList, oldList);
        if (deleteList.size() > 0) {
            tagsArticleMapper.batchRemoveList(articleID, deleteList); // 批量删除标签关系
        }
        if (addList.size() > 0) {
            tagsArticleMapper.batchAddList(articleID, addList); // 批量添加标签关系
        }
    }

    /*处理用户自建标签*/
    public List<Tags> newTagDeal(List<Tags> tagsList) {
        List<Tags> addTagsList = new ArrayList<>(); // 存放要批量添加的tag
        for (int index = 0; index < tagsList.size(); index++) { // 第一次遍历对用户自建标签进行处理
            Tags tag = tagsList.get(index);
            Tags result = checkTag(tag); // 判断数据库是否存在同名标签
            if (tag.getId() == null) {// ID为空说明为用户自建标签
                if (result == null) { // 如果自建标签没有重复则将其放入待添加列表
                    tag.setVersion(0L); // 新标签版本号默认为0
                    addTagsList.add(tag); // 不同名则将其放入待添加列表
                } else {
                    result.setCounts(result.getCounts() + 1); // 已被当前文章引用，热度+1
                    tagsMapper.updateById(result); // 更新标签信息
                    tagsList.set(index, result); // 否则进行替换
                }
            } else { // ID不为空的直接替换并更新
                result.setCounts(result.getCounts() + 1); // 已被当前文章引用，热度+1
                tagsMapper.updateById(result); // 更新标签信息
                tagsList.set(index, result); // 否则进行替换
            }
        }
        if (addTagsList.size() > 0) { // 批量插入新标签到tags表(该方法默认热度为1)
            tagsMapper.batchAddList(addTagsList);
        }
        return tagsList;
    }

    /*检查标签方法*/
    public Tags checkTag(Tags tag) {
        QueryWrapper<Tags> queryWrapper = new QueryWrapper<>();
        // 用户自建标签ID为空,且内容不为空可能是已有标签
        if (StrUtil.isNotBlank(tag.getContent())) {
            queryWrapper.eq("content", tag.getContent()); // 根据文章内容查询并返回
        } else if (StrUtil.isNotBlank(tag.getId().toString())) { // ID不为空必定为已有标签
            queryWrapper.eq("id", tag.getId()); // 根据标签ID查询并返回
        } else { // ID和内容都为空为异常
            throw new ServiceException(Constants.CODE_400, "参数异常!");
        }
        return tagsMapper.selectOne(queryWrapper);
    }

    /*批量查询文章已有标签方法*/
    public List<TagsArticle> getOwnTag(Integer articleID) {
        QueryWrapper<TagsArticle> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("article_id", articleID); // 根据文章id查询其所有标签关系列表
        return tagsArticleMapper.selectList(queryWrapper);
    }

}
