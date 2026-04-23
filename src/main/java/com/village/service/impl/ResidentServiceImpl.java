package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.village.dao.ResidentDao;
import com.village.dto.ResidentDTO;
import com.village.dto.ResidentQueryDTO;
import com.village.dto.ResidentStatisticsDTO;
import com.village.entity.Resident;
import com.village.exception.BusinessException;
import com.village.service.ResidentService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 村民档案服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResidentServiceImpl implements ResidentService {

    private final ResidentDao residentDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resident create(ResidentDTO dto) {
        // 校验身份证号唯一性
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resident::getIdCard, dto.getIdCard());
        Long count = residentDao.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("身份证号已存在");
        }

        // 转换为实体
        Resident resident = toEntity(dto);
        resident.setHouseholdStatus("normal");
        resident.setCreateTime(DateUtils.now());
        resident.setUpdateTime(DateUtils.now());
        resident.setDeleted(0);

        residentDao.insert(resident);
        log.info("新增村民档案成功，id={}, name={}", resident.getId(), resident.getName());
        return resident;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Resident update(ResidentDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("ID不能为空");
        }

        Resident existing = residentDao.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("记录不存在");
        }

        // 校验身份证号唯一性（排除自身）
        if (StringUtils.hasText(dto.getIdCard()) && !dto.getIdCard().equals(existing.getIdCard())) {
            LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Resident::getIdCard, dto.getIdCard());
            Long count = residentDao.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException("身份证号已存在");
            }
        }

        // 更新字段
        existing.setName(dto.getName());
        existing.setIdCard(dto.getIdCard());
        existing.setGender(dto.getGender());
        existing.setBirthDate(dto.getBirthDate());
        existing.setPersonType(dto.getPersonType());
        existing.setSecurityType(dto.getSecurityType());
        existing.setPhone(dto.getPhone());
        existing.setAddress(dto.getAddress());
        existing.setHouseholdHead(dto.getHouseholdHead());
        existing.setRelationship(dto.getRelationship());
        existing.setVillage(dto.getVillage());
        existing.setIsLocalHousehold(dto.getIsLocalHousehold());
        existing.setIsHouseholdHead(dto.getIsHouseholdHead());
        existing.setIsLocalResident(dto.getIsLocalResident());
        existing.setExternalAddress(dto.getExternalAddress());
        existing.setRemark(dto.getRemark());
        
        residentDao.updateById(existing);
        log.info("更新村民档案成功，id={}", existing.getId());
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Resident resident = residentDao.selectById(id);
        if (resident == null) {
            throw new BusinessException("记录不存在");
        }

        // 使用 wrapper 方式更新，避免被 @TableLogic 拦截
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resident::getId, id);
        Resident update = new Resident();
        update.setDeleted(1);
        residentDao.update(update, wrapper);
        log.info("删除村民档案成功，id={}", id);
    }

    @Override
    public Resident getById(Long id) {
        Resident resident = residentDao.selectById(id);
        if (resident == null) {
            throw new BusinessException("记录不存在");
        }
        return resident;
    }

    @Override
    public List<Resident> list(ResidentQueryDTO dto) {
        // 构建查询条件
        LambdaQueryWrapper<Resident> wrapper = buildQueryWrapper(dto);

        // 排序
        if (StringUtils.hasText(dto.getOrderBy())) {
            if ("asc".equalsIgnoreCase(dto.getSortOrder())) {
                wrapper.orderByAsc(Resident::getCreateTime);
            } else {
                wrapper.orderByDesc(Resident::getCreateTime);
            }
        }

        // 分页
        Page<Resident> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<Resident> result = residentDao.selectPage(page, wrapper);
        return result.getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancel(Long id) {
        Resident resident = residentDao.selectById(id);
        if (resident == null) {
            throw new BusinessException("记录不存在");
        }

        // 销户登记
        resident.setHouseholdStatus("cancelled");
        residentDao.updateById(resident);
        log.info("销户登记成功，id={}", id);
    }

    @Override
    public ResidentStatisticsDTO statistics() {
        ResidentStatisticsDTO dto = new ResidentStatisticsDTO();

        // 查询所有未删除的村民
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resident::getDeleted, 0);
        List<Resident> residents = residentDao.selectList(wrapper);

        dto.setTotalCount((long) residents.size());

        // 常住人口数（is_local_resident = 1）
        long localResidentCount = residents.stream()
                .filter(r -> r.getIsLocalResident() != null && r.getIsLocalResident() == 1)
                .count();
        dto.setLocalResidentCount(localResidentCount);

        // 就读学生数（person_type = 'student'）
        long studentCount = residents.stream()
                .filter(r -> "student".equals(r.getPersonType()))
                .count();
        dto.setStudentCount(studentCount);

        // 低保人数（security_type 包含 'allowance'）
        long allowanceCount = residents.stream()
                .filter(r -> StringUtils.hasText(r.getSecurityType()) && r.getSecurityType().contains("allowance"))
                .count();
        dto.setAllowanceCount(allowanceCount);

        // 五保户人数（security_type 包含 'five_guarantee'）
        long fiveGuaranteeCount = residents.stream()
                .filter(r -> StringUtils.hasText(r.getSecurityType()) && r.getSecurityType().contains("five_guarantee"))
                .count();
        dto.setFiveGuaranteeCount(fiveGuaranteeCount);

        // 非本地户籍人数（is_local_household = 0）
        long nonLocalHouseholdCount = residents.stream()
                .filter(r -> r.getIsLocalHousehold() != null && r.getIsLocalHousehold() == 0)
                .count();
        dto.setNonLocalHouseholdCount(nonLocalHouseholdCount);

        // 按户籍状态统计
        Map<String, Long> householdStatusCount = residents.stream()
                .collect(Collectors.groupingBy(
                        r -> r.getHouseholdStatus() == null ? "normal" : r.getHouseholdStatus(),
                        Collectors.counting()
                ));
        dto.setHouseholdStatusCount(householdStatusCount);

        // 按人员类型统计
        Map<String, Long> personTypeCount = residents.stream()
                .filter(r -> StringUtils.hasText(r.getPersonType()))
                .collect(Collectors.groupingBy(Resident::getPersonType, Collectors.counting()));
        dto.setPersonTypeCount(personTypeCount);

        // 按保障类型统计
        Map<String, Long> securityTypeCount = residents.stream()
                .filter(r -> StringUtils.hasText(r.getSecurityType()))
                .collect(Collectors.groupingBy(Resident::getSecurityType, Collectors.counting()));
        dto.setSecurityTypeCount(securityTypeCount);

        // 按村组统计
        Map<String, Long> villageCount = residents.stream()
                .filter(r -> StringUtils.hasText(r.getVillage()))
                .collect(Collectors.groupingBy(Resident::getVillage, Collectors.counting()));
        dto.setVillageCount(villageCount);

        return dto;
    }

    @Override
    public List<Resident> listForExport(ResidentQueryDTO dto) {
        LambdaQueryWrapper<Resident> wrapper = buildQueryWrapper(dto);
        wrapper.orderByDesc(Resident::getCreateTime);
        return residentDao.selectList(wrapper);
    }

    /**
     * 构建查询条件
     */
    private LambdaQueryWrapper<Resident> buildQueryWrapper(ResidentQueryDTO dto) {
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resident::getDeleted, 0);

        if (StringUtils.hasText(dto.getName())) {
            wrapper.like(Resident::getName, dto.getName());
        }
        if (StringUtils.hasText(dto.getIdCard())) {
            wrapper.like(Resident::getIdCard, dto.getIdCard());
        }
        if (StringUtils.hasText(dto.getGender())) {
            wrapper.eq(Resident::getGender, dto.getGender());
        }
        if (StringUtils.hasText(dto.getHouseholdStatus())) {
            wrapper.eq(Resident::getHouseholdStatus, dto.getHouseholdStatus());
        }
        if (StringUtils.hasText(dto.getPersonType())) {
            wrapper.eq(Resident::getPersonType, dto.getPersonType());
        }
        if (StringUtils.hasText(dto.getSecurityType())) {
            wrapper.eq(Resident::getSecurityType, dto.getSecurityType());
        }
        if (StringUtils.hasText(dto.getVillage())) {
            wrapper.like(Resident::getVillage, dto.getVillage());
        }
        if (dto.getBirthDateStart() != null) {
            wrapper.ge(Resident::getBirthDate, dto.getBirthDateStart());
        }
        if (dto.getBirthDateEnd() != null) {
            wrapper.le(Resident::getBirthDate, dto.getBirthDateEnd());
        }

        return wrapper;
    }

    /**
     * DTO 转换为实体
     */
    private Resident toEntity(ResidentDTO dto) {
        Resident resident = new Resident();
        resident.setName(dto.getName());
        resident.setIdCard(dto.getIdCard());
        resident.setGender(dto.getGender());
        resident.setBirthDate(dto.getBirthDate());
        resident.setHouseholdStatus(dto.getHouseholdStatus());
        resident.setPersonType(dto.getPersonType());
        resident.setSecurityType(dto.getSecurityType());
        resident.setPhone(dto.getPhone());
        resident.setAddress(dto.getAddress());
        resident.setHouseholdHead(dto.getHouseholdHead());
        resident.setRelationship(dto.getRelationship());
        resident.setVillage(dto.getVillage());
        resident.setIsLocalHousehold(dto.getIsLocalHousehold());
        resident.setIsHouseholdHead(dto.getIsHouseholdHead());
        resident.setIsLocalResident(dto.getIsLocalResident());
        resident.setExternalAddress(dto.getExternalAddress());
        resident.setRemark(dto.getRemark());
        return resident;
    }
}