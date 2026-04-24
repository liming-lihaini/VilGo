package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.Task;
import org.apache.ibatis.annotations.Mapper;

/**
 * 任务数据访问层
 */
@Mapper
public interface TaskDao extends BaseMapper<Task> {
}
