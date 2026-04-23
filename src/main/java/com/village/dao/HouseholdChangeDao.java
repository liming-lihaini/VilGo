package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.HouseholdChange;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 户籍变动数据访问层
 */
@Mapper
public interface HouseholdChangeDao extends BaseMapper<HouseholdChange> {

    /**
     * 根据家庭户ID查询变动记录
     */
    @Select("SELECT * FROM household_changes WHERE household_id = #{householdId} AND deleted = 0 ORDER BY change_time DESC")
    java.util.List<HouseholdChange> selectByHouseholdId(@Param("householdId") Long householdId);
}