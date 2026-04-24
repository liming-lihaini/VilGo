package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.Meeting;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会议记录数据访问层
 */
@Mapper
public interface MeetingDao extends BaseMapper<Meeting> {
}
