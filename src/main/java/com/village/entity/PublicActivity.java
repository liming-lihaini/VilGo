package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 公益活动实体类
 * 对应数据库表 public_activities
 */
@Data
@TableName("public_activities")
public class PublicActivity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String activityType;

    private String startTime;

    private String endTime;

    private String location;

    private String content;

    private String requirement;

    private String status;

    private String summary;

    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    @TableLogic
    private Integer deleted;
}