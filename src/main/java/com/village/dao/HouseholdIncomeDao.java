package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.HouseholdIncome;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 年度收入数据访问层
 */
@Mapper
public interface HouseholdIncomeDao extends BaseMapper<HouseholdIncome> {

    /**
     * 根据家庭户ID和年份查询收入记录
     */
    @Select("SELECT * FROM household_income WHERE household_id = #{householdId} AND year = #{year} AND deleted = 0")
    HouseholdIncome selectByHouseholdIdAndYear(@Param("householdId") Long householdId, @Param("year") Integer year);
}