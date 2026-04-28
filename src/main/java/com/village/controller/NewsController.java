package com.village.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.village.dto.NewsCreateDTO;
import com.village.dto.NewsDTO;
import com.village.dto.NewsQueryDTO;
import com.village.dto.Result;
import com.village.entity.News;
import com.village.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 新闻动态控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/news")
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    /**
     * 创建新闻
     */
    @PostMapping("/create")
    public Result<News> create(@Valid @RequestBody NewsCreateDTO dto) {
        log.info("创建新闻，title={}", dto.getTitle());
        News news = newsService.create(dto);
        return Result.success(news);
    }

    /**
     * 更新新闻
     */
    @PutMapping("/update")
    public Result<News> update(@Valid @RequestBody NewsCreateDTO dto) {
        log.info("更新新闻，id={}", dto.getId());
        News news = newsService.update(dto);
        return Result.success(news);
    }

    /**
     * 删除新闻
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除新闻，id={}", id);
        newsService.delete(id);
        return Result.success();
    }

    /**
     * 归档新闻
     */
    @PutMapping("/archive/{id}")
    public Result<Void> archive(@PathVariable Long id) {
        log.info("归档新闻，id={}", id);
        newsService.archive(id);
        return Result.success();
    }

    /**
     * 获取新闻详情
     */
    @GetMapping("/get/{id}")
    public Result<News> get(@PathVariable Long id) {
        log.info("获取新闻详情，id={}", id);
        News news = newsService.getById(id);
        return Result.success(news);
    }

    /**
     * 新闻列表分页查询
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody NewsQueryDTO dto) {
        log.info("查询新闻列表");
        IPage<NewsDTO> page = newsService.list(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", page.getRecords());
        result.put("total", page.getTotal());
        result.put("pageNum", page.getCurrent());
        result.put("pageSize", page.getSize());
        return Result.success(result);
    }

    /**
     * 获取分类列表
     */
    @GetMapping("/category/list")
    public Result<List<String>> getCategoryList() {
        log.info("获取新闻分类列表");
        List<String> categories = newsService.getCategoryList();
        return Result.success(categories);
    }
}
