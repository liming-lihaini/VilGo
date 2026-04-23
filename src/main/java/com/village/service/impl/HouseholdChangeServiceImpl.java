package com.village.service.impl;

import com.village.dao.HouseholdChangeDao;
import com.village.dto.HouseholdChangeDTO;
import com.village.entity.HouseholdChange;
import com.village.exception.BusinessException;
import com.village.service.HouseholdChangeService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 户籍变动服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HouseholdChangeServiceImpl implements HouseholdChangeService {

    private final HouseholdChangeDao householdChangeDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HouseholdChange createChange(HouseholdChangeDTO dto) {
        if (dto.getHouseholdId() == null) {
            throw new BusinessException("家庭户ID不能为空");
        }
        if (dto.getChangeType() == null) {
            throw new BusinessException("变动类型不能为空");
        }

        HouseholdChange change = new HouseholdChange();
        change.setHouseholdId(dto.getHouseholdId());
        change.setChangeType(dto.getChangeType());
        change.setChangeTime(dto.getChangeTime() != null ? dto.getChangeTime() : DateUtils.now());
        change.setChangeReason(dto.getChangeReason());
        change.setRelatedPersons(dto.getRelatedPersons());
        change.setBeforeStatus(dto.getBeforeStatus());
        change.setAfterStatus(dto.getAfterStatus());
        change.setRemark(dto.getRemark());
        change.setCreateTime(DateUtils.now());
        change.setDeleted(0);

        householdChangeDao.insert(change);
        log.info("登记户籍变动成功，id={}, type={}", change.getId(), change.getChangeType());
        return change;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteChange(Long id) {
        HouseholdChange change = householdChangeDao.selectById(id);
        if (change == null) {
            throw new BusinessException("记录不存在");
        }

        change.setDeleted(1);
        householdChangeDao.updateById(change);
        log.info("删除户籍变动记录成功，id={}", id);
    }

    @Override
    public List<HouseholdChange> listByHouseholdId(Long householdId) {
        return householdChangeDao.selectByHouseholdId(householdId);
    }
}