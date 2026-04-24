package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 任务实体类
 * 对应数据库表 tasks
 */
@Data
@TableName("tasks")
public class Task {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Integer year;

    private String assigner;

    private Long assigneeId;

    private String content;

    private String deadline;

    private String status;

    private String result;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    @TableLogic
    private Integer deleted;
}
