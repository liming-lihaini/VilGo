package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.Resident;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 村民档案数据访问层
 */
@Mapper
public interface ResidentDao extends BaseMapper<Resident> {

    /**
     * 根据身份证号查询（忽略软删除）
     */
    @Select("SELECT * FROM residents WHERE id_card = #{idCard}")
    Resident selectByIdCard(@Param("idCard") String idCard);
}