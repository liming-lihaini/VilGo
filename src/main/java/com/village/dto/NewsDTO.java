package com.village.dto;

import lombok.Data;

/**
 * 新闻动态响应 DTO
 */
@Data
public class NewsDTO {

    private Long id;

    private String title;

    private String content;

    private String coverImage;

    private String keywords;

    private String category;

    private String categoryDisplay;

    private String publishTime;

    private String status;

    private String statusDisplay;

    private String creator;

    private String createTime;

    private String updateTime;
}
