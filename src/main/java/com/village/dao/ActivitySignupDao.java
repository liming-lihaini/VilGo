package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.ActivitySignup;
import org.apache.ibatis.annotations.Mapper;

/**
 * 活动报名数据访问层
 */
@Mapper
public interface ActivitySignupDao extends BaseMapper<ActivitySignup> {
}