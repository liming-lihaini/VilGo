package com.village.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 公示反馈提交 DTO
 */
@Data
public class NoticeFeedbackDTO {

    @NotNull(message = "公示ID不能为空")
    private Long noticeId;

    private Long residentId;

    @NotBlank(message = "反馈内容不能为空")
    private String content;
}
