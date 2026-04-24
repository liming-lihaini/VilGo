package com.village.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 公益活动创建/更新 DTO
 */
@Data
public class PublicActivityDTO {

    private Long id;

    @NotBlank(message = "活动名称不能为空")
    private String name;

    private String activityType;

    @NotBlank(message = "开始时间不能为空")
    private String startTime;

    @NotBlank(message = "结束时间不能为空")
    private String endTime;

    private String location;

    private String content;

    private String requirement;

    private String status;

    private String summary;

    private String creator;
}