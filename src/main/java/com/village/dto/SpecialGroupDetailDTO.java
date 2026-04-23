package com.village.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 特殊人群详情出参 DTO
 */
@Data
public class SpecialGroupDetailDTO {

    private SpecialGroupDTO specialGroup;

    private List<HelpRecordDTO> helpRecords;

    private Integer daysUntilRemind;
}