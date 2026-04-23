package com.village.dto;

import lombok.Data;
import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

/**
 * 年度收入入参 DTO
 */
@Data
public class HouseholdIncomeDTO {

    /**
     * 主键ID（编辑时使用）
     */
    private Long id;

    /**
     * 家庭户ID
     */
    private Long householdId;

    /**
     * 年份
     */
    @NotNull(message = "年份不能为空")
    private Integer year;

    /**
     * 家庭总收入
     */
    private BigDecimal totalIncome;

    /**
     * 人均收入
     */
    private BigDecimal perCapitaIncome;

    /**
     * 收入来源
     */
    private String incomeSource;

    /**
     * 备注
     */
    private String remark;
}