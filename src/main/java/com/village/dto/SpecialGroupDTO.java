package com.village.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 特殊人群新增/编辑入参 DTO
 */
@Data
public class SpecialGroupDTO {

    private Long id;

    private Long residentId;

    @NotBlank(message = "身份证号不能为空")
    private String idCard;

    private String name;

    private String gender;

    private String birthDate;

    private String phone;

    private String address;

    @NotBlank(message = "人群类型不能为空")
    private String groupType;

    private Long helperId;

    private String helperName;

    private String helpContent;

    private String helpTime;

    private String helpResult;

    private String helpStatus;

    private String creator;
}