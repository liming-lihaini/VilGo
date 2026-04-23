package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 家庭成员实体类
 * 对应数据库表 household_members
 */
@Data
@TableName("household_members")
public class HouseholdMember {

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
     * 村民ID（关联 residents.id）
     */
    private Long residentId;

    /**
     * 与户主关系
     */
    private String relation;

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