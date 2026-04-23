package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * 年度收入实体类
 * 对应数据库表 household_income
 */
@Data
@TableName("household_income")
public class HouseholdIncome {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 家庭户ID（关联 households.id）
     */
    private Long householdId;

    /**
     * 年份
     */
    private Integer year;

    /**
     * 家庭总收入
     */
    private BigDecimal totalIncome;

    /**
     * 人均收入
     */
    private BigDecimal perCapitaIncome;

    /**
     * 收入来源
     */
    private String incomeSource;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 软删除标记：0-正常，1-已删除
     */
    @TableLogic
    private Integer deleted;
}