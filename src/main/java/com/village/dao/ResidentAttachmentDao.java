package com.village.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.village.entity.ResidentAttachment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 村民附件数据访问层
 */
@Mapper
public interface ResidentAttachmentDao extends BaseMapper<ResidentAttachment> {
}
