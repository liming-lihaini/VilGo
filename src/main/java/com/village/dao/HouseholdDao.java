package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.Household;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 家庭户数据访问层
 */
@Mapper
public interface HouseholdDao extends BaseMapper<Household> {

    /**
     * 根据户主ID查询家庭户
     */
    @Select("SELECT * FROM households WHERE head_id = #{headId} AND deleted = 0")
    Household selectByHeadId(@Param("headId") Long headId);

    /**
     * 根据家庭户编号查询
     */
    @Select("SELECT * FROM households WHERE household_no = #{householdNo} AND deleted = 0")
    Household selectByHouseholdNo(@Param("householdNo") String householdNo);
}