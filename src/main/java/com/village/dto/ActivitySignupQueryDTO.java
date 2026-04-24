package com.village.dto;

import lombok.Data;

/**
 * 活动报名查询 DTO
 */
@Data
public class ActivitySignupQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private Long activityId;

    private Long residentId;

    private String status;
}