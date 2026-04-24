package com.village.dto;

import lombok.Data;

/**
 * 任务查询 DTO
 */
@Data
public class TaskQueryDTO {

    private Long assigneeId;

    private String status;

    private Integer year;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
