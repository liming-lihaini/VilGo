package com.village.dto;

import lombok.Data;

/**
 * 家庭成员详情 DTO（包含村民信息和与户主关系）
 */
@Data
public class HouseholdMemberDetailDTO {

    /**
     * 家庭成员关系ID（用于删除操作）
     */
    private Long id;

    /**
     * 村民ID
     */
    private Long residentId;

    /**
     * 姓名
     */
    private String name;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 性别
     */
    private String gender;

    /**
     * 联系电话
     */
    private String phone;

    /**
     * 住址
     */
    private String address;

    /**
     * 与户主关系
     */
    private String relation;
}