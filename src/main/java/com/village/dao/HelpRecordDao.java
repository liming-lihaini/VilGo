package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.HelpRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 帮扶记录数据访问层
 */
@Mapper
public interface HelpRecordDao extends BaseMapper<HelpRecord> {

    @Select("SELECT * FROM help_records WHERE special_group_id = #{specialGroupId} AND deleted = 0 ORDER BY help_time DESC")
    List<HelpRecord> selectBySpecialGroupId(@Param("specialGroupId") Long specialGroupId);
}