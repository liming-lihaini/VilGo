package com.village.dto;

import lombok.Data;

/**
 * 家庭成员入参 DTO
 */
@Data
public class HouseholdMemberDTO {

    /**
     * 主键ID（编辑时使用）
     */
    private Long id;

    /**
     * 家庭户ID
     */
    private Long householdId;

    /**
     * 村民ID
     */
    private Long residentId;

    /**
     * 与户主关系
     */
    private String relation;
}