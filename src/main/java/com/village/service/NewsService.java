package com.village.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.village.dto.NewsDTO;
import com.village.dto.NewsCreateDTO;
import com.village.dto.NewsQueryDTO;
import com.village.entity.News;

import java.util.List;

/**
 * 新闻动态服务接口
 */
public interface NewsService {

    /**
     * 创建新闻
     *
     * @param dto 新闻创建DTO
     * @return 新闻实体
     */
    News create(NewsCreateDTO dto);

    /**
     * 更新新闻
     *
     * @param dto 新闻更新DTO
     * @return 新闻实体
     */
    News update(NewsCreateDTO dto);

    /**
     * 删除新闻（软删除）
     *
     * @param id 新闻ID
     */
    void delete(Long id);

    /**
     * 归档新闻
     *
     * @param id 新闻ID
     */
    void archive(Long id);

    /**
     * 根据ID获取新闻
     *
     * @param id 新闻ID
     * @return 新闻实体
     */
    News getById(Long id);

    /**
     * 分页查询新闻列表
     *
     * @param dto 查询条件
     * @return 分页结果
     */
    IPage<NewsDTO> list(NewsQueryDTO dto);

    /**
     * 获取分类列表
     *
     * @return 分类列表
     */
    List<String> getCategoryList();
}
