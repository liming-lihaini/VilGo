package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 新闻动态实体类
 * 对应数据库表 news
 */
@Data
@TableName("news")
public class News {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 新闻标题（全局唯一）
     */
    private String title;

    /**
     * 新闻内容（HTML 格式）
     */
    private String content;

    /**
     * 封面图片路径
     */
    private String coverImage;

    /**
     * 关键词，多个用逗号分隔
     */
    private String keywords;

    /**
     * 新闻分类
     */
    private String category;

    /**
     * 发布时间
     */
    private String publishTime;

    /**
     * 状态：published-已发布，archived-已归档
     */
    private String status;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    /**
     * 软删除标记：0-正常，1-已删除
     */
    @TableLogic
    private Integer deleted;
}
