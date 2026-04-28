package com.village.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 公示创建/更新 DTO
 */
@Data
public class NoticeDTO {

    private Long id;

    @NotBlank(message = "公示标题不能为空")
    private String title;

    @NotBlank(message = "公示内容不能为空")
    private String content;

    private String noticeType;

    private String publisher;

    private String expireDate;

    private String creator;
}
