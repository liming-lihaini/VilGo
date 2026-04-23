package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 帮扶记录实体类
 * 对应数据库表 help_records
 */
@Data
@TableName("help_records")
public class HelpRecord {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long specialGroupId;

    private String helpContent;

    private String helpTime;

    private String helpResult;

    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    @TableLogic
    private Integer deleted;
}