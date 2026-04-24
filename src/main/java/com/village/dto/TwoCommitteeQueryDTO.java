package com.village.dto;

import lombok.Data;

/**
 * 两委成员查询 DTO
 */
@Data
public class TwoCommitteeQueryDTO {

    private String name;

    private String position;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
