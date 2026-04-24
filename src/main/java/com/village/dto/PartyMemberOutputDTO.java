package com.village.dto;

import lombok.Data;

/**
 * 党员档案输出 DTO（包含关联的村民姓名）
 */
@Data
public class PartyMemberOutputDTO {

    /**
     * 主键ID
     */
    private Long id;

    /**
     * 身份证号
     */
    private String idCard;

    /**
     * 姓名（来自 residents 表）
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
     * 党员状态
     */
    private String status;

    /**
     * 创建人
     */
    private String creator;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;
}