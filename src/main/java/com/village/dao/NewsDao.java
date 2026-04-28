package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.village.entity.News;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 新闻动态数据访问层
 */
@Mapper
public interface NewsDao extends BaseMapper<News> {

    /**
     * 根据标题查询新闻（用于唯一性校验）
     *
     * @param title 新闻标题
     * @return 新闻实体
     */
    @Select("SELECT * FROM news WHERE title = #{title} AND deleted = 0")
    News selectByTitle(@Param("title") String title);

    /**
     * 根据标题查询新闻（排除指定ID，用于更新时唯一性校验）
     *
     * @param title 新闻标题
     * @param id    排除的新闻ID
     * @return 新闻实体
     */
    @Select("SELECT * FROM news WHERE title = #{title} AND id != #{id} AND deleted = 0")
    News selectByTitleExcludeId(@Param("title") String title, @Param("id") Long id);

    /**
     * 分页查询新闻列表（带条件）
     *
     * @param page         分页对象
     * @param title        标题（模糊查询）
     * @param category     分类
     * @param status       状态
     * @param startTime    开始时间
     * @param endTime      结束时间
     * @return 分页结果
     */
    @Select("<script>" +
            "SELECT * FROM news WHERE deleted = 0" +
            "<if test='title != null and title != \"\"'> AND title LIKE '%' || #{title} || '%'</if>" +
            "<if test='category != null and category != \"\"'> AND category = #{category}</if>" +
            "<if test='status != null and status != \"\"'> AND status = #{status}</if>" +
            "<if test='startTime != null and startTime != \"\"'> AND publish_time >= #{startTime}</if>" +
            "<if test='endTime != null and endTime != \"\"'> AND publish_time &lt;= #{endTime}</if>" +
            " ORDER BY publish_time DESC" +
            "</script>")
    IPage<News> selectPageWithConditions(
            Page<News> page,
            @Param("title") String title,
            @Param("category") String category,
            @Param("status") String status,
            @Param("startTime") String startTime,
            @Param("endTime") String endTime
    );
}
