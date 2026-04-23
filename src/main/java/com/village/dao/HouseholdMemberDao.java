package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.HouseholdMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 家庭成员数据访问层
 */
@Mapper
public interface HouseholdMemberDao extends BaseMapper<HouseholdMember> {

    /**
     * 根据家庭户ID查询成员列表
     */
    @Select("SELECT * FROM household_members WHERE household_id = #{householdId} AND deleted = 0")
    java.util.List<HouseholdMember> selectByHouseholdId(@Param("householdId") Long householdId);

    /**
     * 查询某村民是否已是某家庭户成员
     */
    @Select("SELECT COUNT(*) FROM household_members WHERE resident_id = #{residentId} AND household_id != #{excludeHouseholdId} AND deleted = 0")
    int countByResidentIdExcludeHousehold(@Param("residentId") Long residentId, @Param("excludeHouseholdId") Long excludeHouseholdId);
}