package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.PartyMember;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 党员档案数据访问层
 */
@Mapper
public interface PartyMemberDao extends BaseMapper<PartyMember> {

    /**
     * 根据身份证号查询（忽略软删除）
     */
    @Select("SELECT * FROM party_members WHERE id_card = #{idCard}")
    PartyMember selectByIdCard(@Param("idCard") String idCard);

    /**
     * 根据身份证号查询（排除软删除）
     */
    @Select("SELECT * FROM party_members WHERE id_card = #{idCard} AND deleted = 0")
    PartyMember selectByIdCardIgnoreDeleted(@Param("idCard") String idCard);
}