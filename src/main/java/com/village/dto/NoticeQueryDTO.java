package com.village.dto;

import lombok.Data;

/**
 * 公示查询 DTO
 */
@Data
public class NoticeQueryDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String title;

    private String noticeType;

    private String startTime;

    private String endTime;
}
