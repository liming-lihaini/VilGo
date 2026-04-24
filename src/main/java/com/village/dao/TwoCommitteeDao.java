package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.TwoCommittee;
import org.apache.ibatis.annotations.Mapper;

/**
 * 两委班子成员数据访问层
 */
@Mapper
public interface TwoCommitteeDao extends BaseMapper<TwoCommittee> {
}
