package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.village.dao.TwoCommitteeDao;
import com.village.dto.TwoCommitteeDTO;
import com.village.dto.TwoCommitteeQueryDTO;
import com.village.entity.TwoCommittee;
import com.village.exception.BusinessException;
import com.village.service.TwoCommitteeService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class TwoCommitteeServiceImpl implements TwoCommitteeService {

    private final TwoCommitteeDao twoCommitteeDao;
    private final com.village.dao.TaskDao taskDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TwoCommittee create(TwoCommitteeDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException("姓名不能为空");
        }

        LambdaQueryWrapper<TwoCommittee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TwoCommittee::getName, dto.getName());
        wrapper.eq(TwoCommittee::getDeleted, 0);
        Long count = twoCommitteeDao.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("已存在同名成员");
        }

        TwoCommittee member = new TwoCommittee();
        member.setResidentId(dto.getResidentId());
        member.setName(dto.getName());
        member.setGender(dto.getGender());
        if (dto.getPositionIds() != null && !dto.getPositionIds().isEmpty()) {
            member.setPositionIds(dto.getPositionIds().stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""));
        }
        if (dto.getPositionNames() != null && !dto.getPositionNames().isEmpty()) {
            member.setPositionNames(String.join(",", dto.getPositionNames()));
        }
        member.setPhone(dto.getPhone());
        member.setDividedWork(dto.getDividedWork());
        member.setJoinDate(dto.getJoinDate());
        member.setCreator(dto.getCreator());
        member.setCreateTime(DateUtils.now());
        member.setUpdateTime(DateUtils.now());
        member.setDeleted(0);

        twoCommitteeDao.insert(member);
        log.info("新增两委成员成功，id={}", member.getId());
        return member;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public TwoCommittee update(TwoCommitteeDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("成员ID不能为空");
        }

        TwoCommittee member = twoCommitteeDao.selectById(dto.getId());
        if (member == null || member.getDeleted() == 1) {
            throw new BusinessException("成员不存在");
        }

        if (StringUtils.hasText(dto.getName())) {
            member.setName(dto.getName());
        }
        member.setResidentId(dto.getResidentId());
        member.setGender(dto.getGender());
        if (dto.getPositionIds() != null && !dto.getPositionIds().isEmpty()) {
            member.setPositionIds(dto.getPositionIds().stream().map(String::valueOf).reduce((a, b) -> a + "," + b).orElse(""));
        }
        if (dto.getPositionNames() != null && !dto.getPositionNames().isEmpty()) {
            member.setPositionNames(String.join(",", dto.getPositionNames()));
        }
        member.setPhone(dto.getPhone());
        member.setDividedWork(dto.getDividedWork());
        member.setJoinDate(dto.getJoinDate());
        member.setUpdateTime(DateUtils.now());

        twoCommitteeDao.updateById(member);
        log.info("更新两委成员成功，id={}", dto.getId());
        return member;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        TwoCommittee member = twoCommitteeDao.selectById(id);
        if (member == null || member.getDeleted() == 1) {
            throw new BusinessException("成员不存在");
        }

        LambdaQueryWrapper<com.village.entity.Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(com.village.entity.Task::getAssigneeId, id);
        wrapper.eq(com.village.entity.Task::getStatus, "ongoing");
        wrapper.eq(com.village.entity.Task::getDeleted, 0);
        Long taskCount = taskDao.selectCount(wrapper);
        if (taskCount > 0) {
            throw new BusinessException("请先完成或转移任务");
        }

        member.setDeleted(1);
        member.setUpdateTime(DateUtils.now());
        twoCommitteeDao.updateById(member);
        log.info("删除两委成员成功，id={}", id);
    }

    @Override
    public TwoCommittee getById(Long id) {
        return twoCommitteeDao.selectById(id);
    }

    @Override
    public List<TwoCommittee> list(TwoCommitteeQueryDTO dto) {
        LambdaQueryWrapper<TwoCommittee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TwoCommittee::getDeleted, 0);

        if (StringUtils.hasText(dto.getName())) {
            wrapper.like(TwoCommittee::getName, dto.getName());
        }

        wrapper.orderByDesc(TwoCommittee::getCreateTime);

        Page<TwoCommittee> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<TwoCommittee> result = twoCommitteeDao.selectPage(page, wrapper);
        return result.getRecords();
    }

    @Override
    public Map<String, Long> statistics() {
        Map<String, Long> stats = new HashMap<>();

        LambdaQueryWrapper<TwoCommittee> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(TwoCommittee::getDeleted, 0);
        stats.put("total", twoCommitteeDao.selectCount(wrapper));

        LambdaQueryWrapper<TwoCommittee> wrapper1 = new LambdaQueryWrapper<>();
        wrapper1.eq(TwoCommittee::getDeleted, 0);
        stats.put("secretary", twoCommitteeDao.selectCount(wrapper1));

        LambdaQueryWrapper<TwoCommittee> wrapper2 = new LambdaQueryWrapper<>();
        wrapper2.eq(TwoCommittee::getDeleted, 0);
        stats.put("director", twoCommitteeDao.selectCount(wrapper2));

        return stats;
    }
}
