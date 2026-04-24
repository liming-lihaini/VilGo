package com.village.dto;

import lombok.Data;

/**
 * 职位新增/更新 DTO
 */
@Data
public class PositionDTO {

    private Long id;

    private String name;

    private String code;

    private Integer sortOrder;
}
