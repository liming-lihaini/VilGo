package com.village.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 党员档案新增/编辑入参 DTO
 */
@Data
public class PartyMemberDTO {

    /**
     * 主键ID（编辑时使用）
     */
    private Long id;

    /**
     * 身份证号
     */
    @NotBlank(message = "身份证号不能为空")
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
     */
    private String status;

    /**
     * 创建人
     */
    private String creator;
}