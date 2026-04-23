package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.village.dao.HouseholdIncomeDao;
import com.village.dto.HouseholdIncomeDTO;
import com.village.entity.HouseholdIncome;
import com.village.exception.BusinessException;
import com.village.service.HouseholdIncomeService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 年度收入服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HouseholdIncomeServiceImpl implements HouseholdIncomeService {

    private final HouseholdIncomeDao householdIncomeDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HouseholdIncome saveIncome(HouseholdIncomeDTO dto) {
        if (dto.getHouseholdId() == null) {
            throw new BusinessException("家庭户ID不能为空");
        }
        if (dto.getYear() == null) {
            throw new BusinessException("年份不能为空");
        }

        // 检查是否已存在该年份的记录
        HouseholdIncome existing = householdIncomeDao.selectByHouseholdIdAndYear(dto.getHouseholdId(), dto.getYear());

        // 计算人均收入
        if (dto.getTotalIncome() != null && dto.getPerCapitaIncome() == null) {
            // 假设家庭人口数从 Household 表获取，暂时使用简单计算
            dto.setPerCapitaIncome(dto.getTotalIncome());
        }

        if (existing != null) {
            // 更新
            existing.setTotalIncome(dto.getTotalIncome());
            existing.setPerCapitaIncome(dto.getPerCapitaIncome());
            existing.setIncomeSource(dto.getIncomeSource());
            existing.setRemark(dto.getRemark());
            householdIncomeDao.updateById(existing);
            log.info("更新年度收入成功，id={}, year={}", existing.getId(), dto.getYear());
            return existing;
        } else {
            // 新增
            HouseholdIncome income = new HouseholdIncome();
            income.setHouseholdId(dto.getHouseholdId());
            income.setYear(dto.getYear());
            income.setTotalIncome(dto.getTotalIncome());
            income.setPerCapitaIncome(dto.getPerCapitaIncome());
            income.setIncomeSource(dto.getIncomeSource());
            income.setRemark(dto.getRemark());
            income.setCreateTime(DateUtils.now());
            income.setDeleted(0);

            householdIncomeDao.insert(income);
            log.info("新增年度收入成功，id={}, year={}", income.getId(), income.getYear());
            return income;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteIncome(Long id) {
        HouseholdIncome income = householdIncomeDao.selectById(id);
        if (income == null) {
            throw new BusinessException("记录不存在");
        }

        income.setDeleted(1);
        householdIncomeDao.updateById(income);
        log.info("删除年度收入成功，id={}", id);
    }

    @Override
    public List<HouseholdIncome> listByHouseholdId(Long householdId) {
        LambdaQueryWrapper<HouseholdIncome> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseholdIncome::getHouseholdId, householdId);
        wrapper.eq(HouseholdIncome::getDeleted, 0);
        wrapper.orderByDesc(HouseholdIncome::getYear);

        return householdIncomeDao.selectList(wrapper);
    }

    @Override
    public HouseholdIncome getByHouseholdIdAndYear(Long householdId, Integer year) {
        return householdIncomeDao.selectByHouseholdIdAndYear(householdId, year);
    }
}