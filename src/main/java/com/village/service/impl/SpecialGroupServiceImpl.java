package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.village.dao.SpecialGroupDao;
import com.village.dao.HelpRecordDao;
import com.village.dto.SpecialGroupDTO;
import com.village.dto.SpecialGroupDetailDTO;
import com.village.dto.SpecialGroupQueryDTO;
import com.village.dto.HelpRecordDTO;
import com.village.entity.SpecialGroup;
import com.village.entity.HelpRecord;
import com.village.entity.Resident;
import com.village.exception.BusinessException;
import com.village.service.SpecialGroupService;
import com.village.service.HelpRecordService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SpecialGroupServiceImpl implements SpecialGroupService {

    private final SpecialGroupDao specialGroupDao;
    private final HelpRecordDao helpRecordDao;
    private final HelpRecordService helpRecordService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpecialGroup create(SpecialGroupDTO dto) {
        LambdaQueryWrapper<SpecialGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialGroup::getIdCard, dto.getIdCard());
        Long count = specialGroupDao.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("该身份证号已存在");
        }

        SpecialGroup group = toEntity(dto);
        group.setCreateTime(DateUtils.now());
        group.setUpdateTime(DateUtils.now());
        group.setDeleted(0);

        specialGroupDao.insert(group);
        log.info("新增特殊人群成功，id={}, name={}", group.getId(), group.getName());
        return group;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SpecialGroup update(SpecialGroupDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("ID不能为空");
        }

        SpecialGroup existing = specialGroupDao.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("记录不存在");
        }

        if (StringUtils.hasText(dto.getIdCard()) && !dto.getIdCard().equals(existing.getIdCard())) {
            LambdaQueryWrapper<SpecialGroup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(SpecialGroup::getIdCard, dto.getIdCard());
            Long count = specialGroupDao.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException("该身份证号已存在");
            }
        }

        existing.setIdCard(dto.getIdCard());
        existing.setName(dto.getName());
        existing.setGender(dto.getGender());
        existing.setBirthDate(dto.getBirthDate());
        existing.setPhone(dto.getPhone());
        existing.setAddress(dto.getAddress());
        existing.setGroupType(dto.getGroupType());
        existing.setHelperId(dto.getHelperId());
        existing.setHelperName(dto.getHelperName());
        existing.setHelpContent(dto.getHelpContent());
        existing.setHelpTime(dto.getHelpTime());
        existing.setHelpResult(dto.getHelpResult());
        existing.setHelpStatus(dto.getHelpStatus());
        existing.setUpdateTime(DateUtils.now());

        specialGroupDao.updateById(existing);
        log.info("更新特殊人群成功，id={}", existing.getId());
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        specialGroupDao.deleteById(id);
        log.info("删除特殊人群成功，id={}", id);
    }

    @Override
    public SpecialGroupDetailDTO getDetail(Long id) {
        SpecialGroup group = specialGroupDao.selectById(id);
        if (group == null) {
            throw new BusinessException("记录不存在");
        }

        SpecialGroupDetailDTO detail = new SpecialGroupDetailDTO();
        detail.setSpecialGroup(toDTO(group));

        List<HelpRecord> records = helpRecordDao.selectBySpecialGroupId(id);
        detail.setHelpRecords(records.stream().map(this::toHelpRecordDTO).collect(Collectors.toList()));

        detail.setDaysUntilRemind(calculateRemindDays(group.getHelpTime()));

        return detail;
    }

    @Override
    public List<SpecialGroup> list(SpecialGroupQueryDTO dto) {
        LambdaQueryWrapper<SpecialGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialGroup::getDeleted, 0);

        if (StringUtils.hasText(dto.getIdCard())) {
            wrapper.like(SpecialGroup::getIdCard, dto.getIdCard());
        }
        if (StringUtils.hasText(dto.getName())) {
            wrapper.like(SpecialGroup::getName, dto.getName());
        }
        if (StringUtils.hasText(dto.getGroupType())) {
            wrapper.eq(SpecialGroup::getGroupType, dto.getGroupType());
        }
        if (StringUtils.hasText(dto.getHelpStatus())) {
            wrapper.eq(SpecialGroup::getHelpStatus, dto.getHelpStatus());
        }
        if (dto.getHelperId() != null) {
            wrapper.eq(SpecialGroup::getHelperId, dto.getHelperId());
        }

        wrapper.orderByDesc(SpecialGroup::getCreateTime);

        Page<SpecialGroup> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<SpecialGroup> result = specialGroupDao.selectPage(page, wrapper);
        return result.getRecords();
    }

    @Override
    public List<SpecialGroup> listForExport(SpecialGroupQueryDTO dto) {
        LambdaQueryWrapper<SpecialGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialGroup::getDeleted, 0);

        if (StringUtils.hasText(dto.getIdCard())) {
            wrapper.like(SpecialGroup::getIdCard, dto.getIdCard());
        }
        if (StringUtils.hasText(dto.getName())) {
            wrapper.like(SpecialGroup::getName, dto.getName());
        }
        if (StringUtils.hasText(dto.getGroupType())) {
            wrapper.eq(SpecialGroup::getGroupType, dto.getGroupType());
        }
        if (StringUtils.hasText(dto.getHelpStatus())) {
            wrapper.eq(SpecialGroup::getHelpStatus, dto.getHelpStatus());
        }

        wrapper.orderByDesc(SpecialGroup::getCreateTime);
        return specialGroupDao.selectList(wrapper);
    }

    @Override
    public Map<String, Long> statistics() {
        LambdaQueryWrapper<SpecialGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialGroup::getDeleted, 0);
        List<SpecialGroup> all = specialGroupDao.selectList(wrapper);

        Map<String, Long> result = new HashMap<>();
        result.put("total", (long) all.size());

        Map<String, Long> groupTypeCount = all.stream()
                .filter(g -> StringUtils.hasText(g.getGroupType()))
                .collect(Collectors.groupingBy(SpecialGroup::getGroupType, Collectors.counting()));
        result.putAll(groupTypeCount);

        Map<String, Long> helpStatusCount = all.stream()
                .filter(g -> StringUtils.hasText(g.getHelpStatus()))
                .collect(Collectors.groupingBy(SpecialGroup::getHelpStatus, Collectors.counting()));
        result.putAll(helpStatusCount);

        return result;
    }

    @Override
    public List<SpecialGroup> getRemindList() {
        LambdaQueryWrapper<SpecialGroup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SpecialGroup::getDeleted, 0);
        wrapper.eq(SpecialGroup::getHelpStatus, "ongoing");
        wrapper.isNotNull(SpecialGroup::getHelpTime);

        List<SpecialGroup> list = specialGroupDao.selectList(wrapper);
        return list.stream()
                .filter(g -> {
                    int days = calculateRemindDays(g.getHelpTime());
                    return days >= 0 && days <= 5;
                })
                .collect(Collectors.toList());
    }

    private int calculateRemindDays(String helpTime) {
        if (!StringUtils.hasText(helpTime)) return -1;
        try {
            LocalDate helpDate = LocalDate.parse(helpTime, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            LocalDate today = LocalDate.now();
            return (int) java.time.temporal.ChronoUnit.DAYS.between(today, helpDate.plusMonths(6));
        } catch (Exception e) {
            return -1;
        }
    }

    private SpecialGroup toEntity(SpecialGroupDTO dto) {
        SpecialGroup group = new SpecialGroup();
        group.setResidentId(dto.getResidentId());
        group.setIdCard(dto.getIdCard());
        group.setName(dto.getName());
        group.setGender(dto.getGender());
        group.setBirthDate(dto.getBirthDate());
        group.setPhone(dto.getPhone());
        group.setAddress(dto.getAddress());
        group.setGroupType(dto.getGroupType());
        group.setHelperId(dto.getHelperId());
        group.setHelperName(dto.getHelperName());
        group.setHelpContent(dto.getHelpContent());
        group.setHelpTime(dto.getHelpTime());
        group.setHelpResult(dto.getHelpResult());
        group.setHelpStatus(dto.getHelpStatus() != null ? dto.getHelpStatus() : "ongoing");
        group.setCreator(dto.getCreator());
        return group;
    }

    private SpecialGroupDTO toDTO(SpecialGroup group) {
        SpecialGroupDTO dto = new SpecialGroupDTO();
        dto.setId(group.getId());
        dto.setResidentId(group.getResidentId());
        dto.setIdCard(group.getIdCard());
        dto.setName(group.getName());
        dto.setGender(group.getGender());
        dto.setBirthDate(group.getBirthDate());
        dto.setPhone(group.getPhone());
        dto.setAddress(group.getAddress());
        dto.setGroupType(group.getGroupType());
        dto.setHelperId(group.getHelperId());
        dto.setHelperName(group.getHelperName());
        dto.setHelpContent(group.getHelpContent());
        dto.setHelpTime(group.getHelpTime());
        dto.setHelpResult(group.getHelpResult());
        dto.setHelpStatus(group.getHelpStatus());
        dto.setCreator(group.getCreator());
        return dto;
    }

    private HelpRecordDTO toHelpRecordDTO(HelpRecord record) {
        HelpRecordDTO dto = new HelpRecordDTO();
        dto.setId(record.getId());
        dto.setSpecialGroupId(record.getSpecialGroupId());
        dto.setHelpContent(record.getHelpContent());
        dto.setHelpTime(record.getHelpTime());
        dto.setHelpResult(record.getHelpResult());
        dto.setCreator(record.getCreator());
        return dto;
    }
}