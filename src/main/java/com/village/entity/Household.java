package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 家庭户实体类
 * 对应数据库表 households
 */
@Data
@TableName("households")
public class Household {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 家庭户编号（全局唯一）
     */
    private String householdNo;

    /**
     * 户主ID（关联 residents.id）
     */
    private Long headId;

    /**
     * 户主姓名
     */
    private String headName;

    /**
     * 户主身份证号
     */
    private String headIdCard;

    /**
     * 住址
     */
    private String address;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 家庭成员数量
     */
    private Integer memberCount;

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