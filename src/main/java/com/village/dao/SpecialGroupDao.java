package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.SpecialGroup;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 特殊人群数据访问层
 */
@Mapper
public interface SpecialGroupDao extends BaseMapper<SpecialGroup> {

    @Select("SELECT * FROM special_groups WHERE id_card = #{idCard} AND deleted = 0")
    SpecialGroup selectByIdCard(@Param("idCard") String idCard);
}