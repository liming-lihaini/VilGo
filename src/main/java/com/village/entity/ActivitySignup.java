package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 活动报名实体类
 * 对应数据库表 activity_signups
 */
@Data
@TableName("activity_signups")
public class ActivitySignup {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long activityId;

    private Long residentId;

    private String status;

    private String signInTime;

    private BigDecimal workHours;

    private BigDecimal workValue;

    private String remark;

    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    @TableLogic
    private Integer deleted;
}