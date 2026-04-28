package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.NoticeFeedback;
import org.apache.ibatis.annotations.Mapper;

/**
 * 公示反馈数据访问层
 */
@Mapper
public interface NoticeFeedbackDao extends BaseMapper<NoticeFeedback> {
}
