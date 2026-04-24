package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.PartyActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 党务活动数据访问层
 */
@Mapper
public interface PartyActivityDao extends BaseMapper<PartyActivity> {
}