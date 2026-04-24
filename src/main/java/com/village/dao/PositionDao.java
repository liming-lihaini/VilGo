package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.Position;
import org.apache.ibatis.annotations.Mapper;

/**
 * 职位数据访问层
 */
@Mapper
public interface PositionDao extends BaseMapper<Position> {
}
