package com.village.dto;

import lombok.Data;

/**
 * 特殊人群查询入参 DTO
 */
@Data
public class SpecialGroupQueryDTO {

    private String idCard;

    private String name;

    private String groupType;

    private String helpStatus;

    private Long helperId;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}