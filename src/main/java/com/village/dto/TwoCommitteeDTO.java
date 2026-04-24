package com.village.dto;

import lombok.Data;
import java.util.List;

/**
 * 两委成员新增/更新 DTO
 */
@Data
public class TwoCommitteeDTO {

    private Long id;

    private Long residentId;

    private String name;

    private String gender;

    private List<Long> positionIds;

    private List<String> positionNames;

    private String phone;

    private String dividedWork;

    private String joinDate;

    private String creator;
}
