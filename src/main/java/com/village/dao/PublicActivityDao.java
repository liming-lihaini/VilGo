package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.PublicActivity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公益活动数据访问层
 */
@Mapper
public interface PublicActivityDao extends BaseMapper<PublicActivity> {
}