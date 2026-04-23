package com.village.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 家庭户新增/编辑入参 DTO
 */
@Data
public class HouseholdDTO {

    /**
     * 主键ID（编辑时使用）
     */
    private Long id;

    /**
     * 家庭户编号
     */
    @NotBlank(message = "家庭户编号不能为空")
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
     * 成员人数
     */
    private Integer memberCount;
}