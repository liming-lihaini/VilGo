package com.village.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 活动报名 DTO
 */
@Data
public class ActivitySignupDTO {

    private Long id;

    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    @NotNull(message = "村民ID不能为空")
    private Long residentId;

    private String status;

    private String signInTime;

    private BigDecimal workHours;

    private BigDecimal workValue;

    private String remark;

    private String creator;
}