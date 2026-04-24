package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.PartyDues;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 党费收缴数据访问层
 */
@Mapper
public interface PartyDuesDao extends BaseMapper<PartyDues> {

    /**
     * 根据党员ID和缴费月份查询
     */
    @Select("SELECT * FROM party_dues WHERE member_id = #{memberId} AND pay_month = #{payMonth} AND deleted = 0")
    PartyDues selectByMemberAndMonth(@Param("memberId") Long memberId, @Param("payMonth") String payMonth);

    /**
     * 统计党员党费欠缴情况
     */
    @Select("SELECT COUNT(*) FROM party_dues WHERE status = 'unpaid' AND deleted = 0")
    Long countUnpaid();

    /**
     * 统计党员党费已缴情况
     */
    @Select("SELECT SUM(amount) FROM party_dues WHERE status = 'paid' AND deleted = 0")
    java.math.BigDecimal sumPaid();
}