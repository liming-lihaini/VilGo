package com.village.dto;

import lombok.Data;

/**
 * 公益活动查询 DTO
 */
@Data
public class PublicActivityQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String name;

    private String activityType;

    private String status;

    private String startTime;

    private String endTime;
}