package com.village.dto;

import lombok.Data;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 党费收缴新增/编辑入参 DTO
 */
@Data
public class PartyDuesDTO {

    /**
     * 主键ID（编辑时使用）
     */
    private Long id;

    /**
     * 党员ID
     */
    @NotNull(message = "党员ID不能为空")
    private Long memberId;

    /**
     * 缴费金额
     */
    @NotNull(message = "缴费金额不能为空")
    private BigDecimal amount;

    /**
     * 缴费月份（格式：yyyy-MM）
     */
    private String payMonth;

    /**
     * 缴费日期
     */
    private String payDate;

    /**
     * 缴费状态：unpaid-未缴，paid-已缴
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

    /**
     * 创建人
     */
    private String creator;
}