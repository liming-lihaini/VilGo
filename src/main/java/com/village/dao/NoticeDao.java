package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.Notice;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公示数据访问层
 */
@Mapper
public interface NoticeDao extends BaseMapper<Notice> {
}
