package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 党员档案实体类
 * 对应数据库表 party_members
 */
@Data
@TableName("party_members")
public class PartyMember {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 身份证号（唯一标识，关联 residents 表）
     */
    private String idCard;

    /**
     * 姓名
     */
    private String name;

    /**
     * 性别
     */
    private String gender;

    /**
     * 出生日期
     */
    private String birthDate;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 住址
     */
    private String address;

    /**
     * 入党时间
     */
    private String joinDate;

    /**
     * 转正时间
     */
    private String convertDate;

    /**
     * 党员状态：activist-积极分子，developing-发展对象，probationary-预备党员，formal-正式党员
     * 状态流转顺序：activist → developing → probationary → formal
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