package com.village.dto;

import lombok.Data;

import java.math.BigDecimal;

/**
 * 活动统计 DTO
 */
@Data
public class ActivityStatisticsDTO {

    private Integer totalActivities;

    private Integer totalSignups;

    private Integer totalParticipants;

    private BigDecimal totalWorkHours;

    private BigDecimal totalWorkValue;

    private String activityType;

    private String timeRange;
}