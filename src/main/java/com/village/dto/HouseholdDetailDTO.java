package com.village.dto;

import lombok.Data;

/**
 * 家庭户详情出参 DTO（包含成员和收入信息）
 */
@Data
public class HouseholdDetailDTO {

    /**
     * 家庭户基本信息
     */
    private HouseholdDTO household;

    /**
     * 成员列表
     */
    private java.util.List<ResidentDTO> members;

    /**
     * 收入记录列表
     */
    private java.util.List<HouseholdIncomeDTO> incomes;

    /**
     * 变动记录列表
     */
    private java.util.List<HouseholdChangeDTO> changes;
}