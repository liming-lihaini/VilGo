package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 村民档案实体类
 * 对应数据库表 residents
 */
@Data
@TableName("residents")
public class Resident {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号（全局唯一）
     */
    private String idCard;

    /**
     * 性别
     */
    private String gender;

    /**
     * 出生日期
     */
    private LocalDate birthDate;

    /**
     * 户籍状态：normal-正常，cancelled-已销户
     */
    private String householdStatus;

    /**
     * 人员类型：farmer-农民，worker-工人，teacher-教师，doctor-医生，student-学生，other-其他
     */
    private String personType;

    /**
     * 保障类型：pension-养老保险，insurance-医疗保险，allowance-低保，none-未参保
     */
    private String securityType;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 住址
     */
    private String address;

    /**
     * 户主姓名
     */
    private String householdHead;

    /**
     * 与户主关系
     */
    private String relationship;

    /**
     * 所属村组
     */
    private String village;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /**
     * 软删除标记：0-正常，1-已删除
     */
    @TableLogic
    private Integer deleted;
}