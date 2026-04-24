package com.village.dto;

import lombok.Data;

/**
 * 党费收缴查询条件 DTO
 */
@Data
public class PartyDuesQueryDTO {

    /**
     * 党员ID
     */
    private Long memberId;

    /**
     * 党员姓名（模糊查询）
     */
    private String memberName;

    /**
     * 缴费月份（格式：yyyy-MM）
     */
    private String payMonth;

    /**
     * 缴费状态：unpaid-未缴，paid-已缴
     */
    private String status;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;
}