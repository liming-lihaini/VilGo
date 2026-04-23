package com.village.service;

import com.village.dto.HelpRecordDTO;
import com.village.entity.HelpRecord;
import java.util.List;

/**
 * 帮扶记录服务接口
 */
public interface HelpRecordService {

    HelpRecord create(HelpRecordDTO dto);

    void delete(Long id);

    List<HelpRecord> listBySpecialGroupId(Long specialGroupId);
}