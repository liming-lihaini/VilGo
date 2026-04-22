package com.village.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

/**
 * 村民档案新增/编辑入参 DTO
 */
@Data
public class ResidentDTO {

    /**
     * 主键ID（编辑时使用）
     */
    private Long id;

    /**
     * 姓名
     */
    @NotBlank(message = "姓名不能为空")
    private String name;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
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
}