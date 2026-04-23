package com.village.service;

import com.village.dto.HouseholdIncomeDTO;
import com.village.entity.HouseholdIncome;
import java.util.List;

/**
 * 年度收入服务接口
 */
public interface HouseholdIncomeService {

    /**
     * 新增/更新年度收入
     */
    HouseholdIncome saveIncome(HouseholdIncomeDTO dto);

    /**
     * 删除年度收入（软删除）
     */
    void deleteIncome(Long id);

    /**
     * 查询家庭年度收入列表
     */
    List<HouseholdIncome> listByHouseholdId(Long householdId);

    /**
     * 根据年份查询
     */
    HouseholdIncome getByHouseholdIdAndYear(Long householdId, Integer year);
}