package com.village.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;

/**
 * 帮扶记录新增入参 DTO
 */
@Data
public class HelpRecordDTO {

    private Long id;

    @NotNull(message = "特殊人群ID不能为空")
    private Long specialGroupId;

    private String helpContent;

    private String helpTime;

    private String helpResult;

    private String creator;
}