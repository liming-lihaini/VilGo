package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 户籍变动实体类
 * 对应数据库表 household_changes
 */
@Data
@TableName("household_changes")
public class HouseholdChange {

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
     * 变动类型：move_in-迁入，move_out-迁出，newborn-新生儿，split-分户，merge-合户
     */
    private String changeType;

    /**
     * 变动时间
     */
    private String changeTime;

    /**
     * 变动原因
     */
    private String changeReason;

    /**
     * 相关人员（多人用逗号分隔）
     */
    private String relatedPersons;

    /**
     * 变动前状态
     */
    private String beforeStatus;

    /**
     * 变动后状态
     */
    private String afterStatus;

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