package com.village.service.impl;

import com.village.dao.HelpRecordDao;
import com.village.dto.HelpRecordDTO;
import com.village.entity.HelpRecord;
import com.village.exception.BusinessException;
import com.village.service.HelpRecordService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HelpRecordServiceImpl implements HelpRecordService {

    private final HelpRecordDao helpRecordDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HelpRecord create(HelpRecordDTO dto) {
        if (dto.getSpecialGroupId() == null) {
            throw new BusinessException("特殊人群ID不能为空");
        }

        HelpRecord record = new HelpRecord();
        record.setSpecialGroupId(dto.getSpecialGroupId());
        record.setHelpContent(dto.getHelpContent());
        record.setHelpTime(dto.getHelpTime() != null ? dto.getHelpTime() : DateUtils.now());
        record.setHelpResult(dto.getHelpResult());
        record.setCreator(dto.getCreator());
        record.setCreateTime(DateUtils.now());
        record.setUpdateTime(DateUtils.now());
        record.setDeleted(0);

        helpRecordDao.insert(record);
        log.info("新增帮扶记录成功，id={}", record.getId());
        return record;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        helpRecordDao.deleteById(id);
        log.info("删除帮扶记录成功，id={}", id);
    }

    @Override
    public List<HelpRecord> listBySpecialGroupId(Long specialGroupId) {
        return helpRecordDao.selectBySpecialGroupId(specialGroupId);
    }
}