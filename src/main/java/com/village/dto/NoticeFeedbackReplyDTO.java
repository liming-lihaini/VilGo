package com.village.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 公示反馈回复 DTO
 */
@Data
public class NoticeFeedbackReplyDTO {

    @NotNull(message = "反馈ID不能为空")
    private Long id;

    @NotBlank(message = "回复内容不能为空")
    private String reply;
}
