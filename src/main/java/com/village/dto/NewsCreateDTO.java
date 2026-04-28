package com.village.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 新闻创建/更新 DTO
 */
@Data
public class NewsCreateDTO {

    private Long id;

    @NotBlank(message = "新闻标题不能为空")
    private String title;

    @NotBlank(message = "新闻内容不能为空")
    private String content;

    private String coverImage;

    private String keywords;

    @NotBlank(message = "新闻分类不能为空")
    private String category;

    private String publishTime;

    private String creator;
}
