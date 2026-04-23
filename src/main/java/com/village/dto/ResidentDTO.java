package com.village.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

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
    private String birthDate;

    /**
     * 户籍状态：normal-正常，cancelled-已销户
     */
    private String householdStatus;

    /**
     * 人员类型：farmer-农民，worker-工人，teacher-教师，doctor-医生，student-学生，other-其他
     */
    private String personType;

    /**
     * 保障类型（多选）：pension-社会养老, worker_pension-职工养老, allowance-低保, five_guarantee-五保, other-其他, none-无
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
     * 是否本村户籍：0-否，1-是
     */
    private Integer isLocalHousehold;

    /**
     * 是否户主：0-否，1-是
     */
    private Integer isHouseholdHead;

    /**
     * 是否本村常住：0-否，1-是
     */
    private Integer isLocalResident;

    /**
     * 外地居住地址
     */
    private String externalAddress;

    /**
     * 备注
     */
    private String remark;
}