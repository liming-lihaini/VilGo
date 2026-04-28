package com.village.dto;

import lombok.Data;

/**
 * 新闻查询 DTO
 */
@Data
public class NewsQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String title;

    private String category;

    private String status;

    private String startTime;

    private String endTime;
}
