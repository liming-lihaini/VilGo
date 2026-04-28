package com.village.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.village.dto.NewsDTO;
import com.village.dto.NewsCreateDTO;
import com.village.dto.NewsQueryDTO;
import com.village.entity.News;
import com.village.enums.NewsCategory;
import com.village.exception.BusinessException;
import com.village.service.NewsService;
import com.village.dao.NewsDao;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 新闻动态服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsDao newsDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public News create(NewsCreateDTO dto) {
        // 标题非空校验
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new BusinessException("新闻标题不能为空");
        }

        // 内容非空校验
        if (!StringUtils.hasText(dto.getContent())) {
            throw new BusinessException("新闻内容不能为空");
        }

        // 分类有效性校验
        if (!StringUtils.hasText(dto.getCategory())) {
            throw new BusinessException("新闻分类不能为空");
        }
        if (!NewsCategory.isValid(dto.getCategory())) {
            throw new BusinessException("新闻分类无效");
        }

        // 标题全局唯一性校验
        News existNews = newsDao.selectByTitle(dto.getTitle());
        if (existNews != null) {
            throw new BusinessException("新闻标题已存在");
        }

        News news = new News();
        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setCategory(dto.getCategory());
        news.setCoverImage(dto.getCoverImage());
        news.setKeywords(dto.getKeywords());
        news.setPublishTime(dto.getPublishTime() != null ? dto.getPublishTime() : DateUtils.now());
        news.setStatus("published");
        news.setCreator(dto.getCreator());
        news.setCreateTime(DateUtils.now());
        news.setUpdateTime(DateUtils.now());
        news.setDeleted(0);

        newsDao.insert(news);
        log.info("新增新闻成功，id={}, title={}", news.getId(), news.getTitle());
        return news;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public News update(NewsCreateDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("新闻ID不能为空");
        }

        News news = newsDao.selectById(dto.getId());
        if (news == null || news.getDeleted() == 1) {
            throw new BusinessException("新闻不存在");
        }

        // 归档后不可编辑
        if ("archived".equals(news.getStatus())) {
            throw new BusinessException("已归档的新闻不能编辑");
        }

        // 标题非空校验
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new BusinessException("新闻标题不能为空");
        }

        // 内容非空校验
        if (!StringUtils.hasText(dto.getContent())) {
            throw new BusinessException("新闻内容不能为空");
        }

        // 分类有效性校验
        if (!StringUtils.hasText(dto.getCategory())) {
            throw new BusinessException("新闻分类不能为空");
        }
        if (!NewsCategory.isValid(dto.getCategory())) {
            throw new BusinessException("新闻分类无效");
        }

        // 标题全局唯一性校验（排除自身）
        News existNews = newsDao.selectByTitleExcludeId(dto.getTitle(), dto.getId());
        if (existNews != null) {
            throw new BusinessException("新闻标题已存在");
        }

        news.setTitle(dto.getTitle());
        news.setContent(dto.getContent());
        news.setCategory(dto.getCategory());
        news.setCoverImage(dto.getCoverImage());
        news.setKeywords(dto.getKeywords());
        if (StringUtils.hasText(dto.getPublishTime())) {
            news.setPublishTime(dto.getPublishTime());
        }
        news.setUpdateTime(DateUtils.now());

        newsDao.updateById(news);
        log.info("更新新闻成功，id={}, title={}", news.getId(), news.getTitle());
        return news;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        News news = newsDao.selectById(id);
        if (news == null || news.getDeleted() == 1) {
            throw new BusinessException("新闻不存在");
        }

        // 软删除保留历史
        news.setDeleted(1);
        news.setUpdateTime(DateUtils.now());
        newsDao.updateById(news);
        log.info("删除新闻成功，id={}, title={}", id, news.getTitle());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void archive(Long id) {
        News news = newsDao.selectById(id);
        if (news == null || news.getDeleted() == 1) {
            throw new BusinessException("新闻不存在");
        }

        if ("archived".equals(news.getStatus())) {
            throw new BusinessException("新闻已归档");
        }

        news.setStatus("archived");
        news.setUpdateTime(DateUtils.now());
        newsDao.updateById(news);
        log.info("归档新闻成功，id={}, title={}", id, news.getTitle());
    }

    @Override
    public News getById(Long id) {
        return newsDao.selectById(id);
    }

    @Override
    public IPage<NewsDTO> list(NewsQueryDTO dto) {
        Page<News> page = new Page<>(dto.getPageNum(), dto.getPageSize());

        IPage<News> newsPage = newsDao.selectPageWithConditions(
                page,
                dto.getTitle(),
                dto.getCategory(),
                dto.getStatus(),
                dto.getStartTime(),
                dto.getEndTime()
        );

        // 转换为 DTO
        IPage<NewsDTO> dtoPage = new Page<>(newsPage.getCurrent(), newsPage.getSize(), newsPage.getTotal());
        List<NewsDTO> dtoList = new ArrayList<>();
        for (News news : newsPage.getRecords()) {
            NewsDTO newsDTO = new NewsDTO();
            BeanUtils.copyProperties(news, newsDTO);
            // 设置分类显示名称
            NewsCategory category = NewsCategory.fromCode(news.getCategory());
            if (category != null) {
                newsDTO.setCategoryDisplay(category.getDisplayName());
            }
            // 设置状态显示名称
            if ("published".equals(news.getStatus())) {
                newsDTO.setStatusDisplay("已发布");
            } else if ("archived".equals(news.getStatus())) {
                newsDTO.setStatusDisplay("已归档");
            }
            dtoList.add(newsDTO);
        }
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public List<String> getCategoryList() {
        List<String> categories = new ArrayList<>();
        for (NewsCategory category : NewsCategory.values()) {
            categories.add(category.getCode());
        }
        return categories;
    }
}
