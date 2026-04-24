package com.village.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 任务新增/更新 DTO
 */
@Data
public class TaskDTO {

    private Long id;

    private Integer year;

    private String assigner;

    @NotNull(message = "接收人不能为空")
    private Long assigneeId;

    private String content;

    private String deadline;

    private String status;

    private String result;
}
