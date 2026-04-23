package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.village.dao.HouseholdDao;
import com.village.dao.HouseholdIncomeDao;
import com.village.dao.HouseholdMemberDao;
import com.village.dao.ResidentDao;
import com.village.dto.HouseholdDTO;
import com.village.dto.HouseholdDetailDTO;
import com.village.dto.HouseholdIncomeDTO;
import com.village.dto.HouseholdMemberDTO;
import com.village.dto.HouseholdQueryDTO;
import com.village.entity.Household;
import com.village.entity.HouseholdIncome;
import com.village.entity.HouseholdMember;
import com.village.entity.Resident;
import com.village.exception.BusinessException;
import com.village.service.HouseholdChangeService;
import com.village.service.HouseholdIncomeService;
import com.village.service.HouseholdMemberService;
import com.village.service.HouseholdService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 家庭户服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HouseholdServiceImpl implements HouseholdService {

    private final HouseholdDao householdDao;
    private final HouseholdMemberDao householdMemberDao;
    private final HouseholdIncomeDao householdIncomeDao;
    private final ResidentDao residentDao;
    private final HouseholdMemberService householdMemberService;
    private final HouseholdIncomeService householdIncomeService;
    private final HouseholdChangeService householdChangeService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Household create(HouseholdDTO dto) {
        // 校验编号唯一性
        LambdaQueryWrapper<Household> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Household::getHouseholdNo, dto.getHouseholdNo());
        Long count = householdDao.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("家庭户编号已存在");
        }

        Household household = toEntity(dto);
        household.setMemberCount(0);
        household.setCreateTime(DateUtils.now());
        household.setUpdateTime(DateUtils.now());
        household.setDeleted(0);

        householdDao.insert(household);
        log.info("新增家庭户成功，id={}, householdNo={}", household.getId(), household.getHouseholdNo());
        return household;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Household update(HouseholdDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("ID不能为空");
        }

        Household existing = householdDao.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("记录不存在");
        }

        // 校验编号唯一性（排除自身）
        if (StringUtils.hasText(dto.getHouseholdNo()) && !dto.getHouseholdNo().equals(existing.getHouseholdNo())) {
            LambdaQueryWrapper<Household> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Household::getHouseholdNo, dto.getHouseholdNo());
            Long count = householdDao.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException("家庭户编号已存在");
            }
        }

        existing.setHouseholdNo(dto.getHouseholdNo());
        existing.setHeadId(dto.getHeadId());
        existing.setHeadName(dto.getHeadName());
        existing.setHeadIdCard(dto.getHeadIdCard());
        existing.setAddress(dto.getAddress());
        existing.setPhone(dto.getPhone());
        existing.setUpdateTime(DateUtils.now());

        householdDao.updateById(existing);
        log.info("更新家庭户成功，id={}", existing.getId());
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Household household = householdDao.selectById(id);
        if (household == null) {
            throw new BusinessException("记录不存在");
        }

        // 使用 wrapper 方式更新，避免被 @TableLogic 拦截
        householdDao.deleteById(id);
        log.info("删除家庭户成功，id={}", id);
    }

    @Override
    public HouseholdDetailDTO getDetail(Long id) {
        Household household = householdDao.selectById(id);
        if (household == null) {
            throw new BusinessException("记录不存在");
        }

        HouseholdDetailDTO detail = new HouseholdDetailDTO();

        // 家庭户基本信息
        HouseholdDTO householdDTO = toDTO(household);
        detail.setHousehold(householdDTO);

        // 成员列表
        List<com.village.dto.HouseholdMemberDetailDTO> members = householdMemberService.getMembersByHouseholdId(id);
        detail.setMembers(members);

        // 收入记录
        List<HouseholdIncome> incomes = householdIncomeService.listByHouseholdId(id);
        detail.setIncomes(incomes.stream().map(this::toIncomeDTO).collect(java.util.stream.Collectors.toList()));

        // 变动记录
        detail.setChanges(householdChangeService.listByHouseholdId(id).stream()
                .map(this::toChangeDTO)
                .collect(java.util.stream.Collectors.toList()));

        return detail;
    }

    @Override
    public List<Household> list(HouseholdQueryDTO dto) {
        LambdaQueryWrapper<Household> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Household::getDeleted, 0);

        if (StringUtils.hasText(dto.getHouseholdNo())) {
            wrapper.like(Household::getHouseholdNo, dto.getHouseholdNo());
        }
        if (StringUtils.hasText(dto.getHeadName())) {
            wrapper.like(Household::getHeadName, dto.getHeadName());
        }
        if (StringUtils.hasText(dto.getHeadIdCard())) {
            wrapper.like(Household::getHeadIdCard, dto.getHeadIdCard());
        }
        if (StringUtils.hasText(dto.getAddress())) {
            wrapper.like(Household::getAddress, dto.getAddress());
        }

        wrapper.orderByDesc(Household::getCreateTime);

        Page<Household> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<Household> result = householdDao.selectPage(page, wrapper);
        return result.getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Household syncFromResident(Long residentId) {
        Resident resident = residentDao.selectById(residentId);
        if (resident == null) {
            throw new BusinessException("村民档案不存在");
        }

        if (resident.getIsHouseholdHead() == null || resident.getIsHouseholdHead() != 1) {
            throw new BusinessException("该村民不是户主");
        }

        // 检查是否已存在
        Household existing = householdDao.selectByHeadId(residentId);
        if (existing != null) {
            return existing;
        }

        // 创建家庭户
        Household household = new Household();
        household.setHouseholdNo(generateHouseholdNo());
        household.setHeadId(resident.getId());
        household.setHeadName(resident.getName());
        household.setHeadIdCard(resident.getIdCard());
        household.setAddress(resident.getAddress());
        household.setPhone(resident.getPhone());
        household.setMemberCount(1);
        household.setCreateTime(DateUtils.now());
        household.setUpdateTime(DateUtils.now());
        household.setDeleted(0);

        householdDao.insert(household);
        log.info("从村民档案同步家庭户成功，id={}, householdNo={}", household.getId(), household.getHouseholdNo());
        return household;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void syncAllFromResidents() {
        // 查询所有户主
        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Resident::getIsHouseholdHead, 1);
        wrapper.eq(Resident::getDeleted, 0);
        List<Resident> householdHeads = residentDao.selectList(wrapper);

        for (Resident head : householdHeads) {
            syncFromResident(head.getId());
        }
        log.info("同步所有户主完成，共 {} 条", householdHeads.size());
    }

    /**
     * 生成家庭户编号
     */
    private String generateHouseholdNo() {
        return "HH" + System.currentTimeMillis();
    }

    private Household toEntity(HouseholdDTO dto) {
        Household household = new Household();
        household.setHouseholdNo(dto.getHouseholdNo());
        household.setHeadId(dto.getHeadId());
        household.setHeadName(dto.getHeadName());
        household.setHeadIdCard(dto.getHeadIdCard());
        household.setAddress(dto.getAddress());
        household.setPhone(dto.getPhone());
        return household;
    }

    private HouseholdDTO toDTO(Household household) {
        HouseholdDTO dto = new HouseholdDTO();
        dto.setId(household.getId());
        dto.setHouseholdNo(household.getHouseholdNo());
        dto.setHeadId(household.getHeadId());
        dto.setHeadName(household.getHeadName());
        dto.setHeadIdCard(household.getHeadIdCard());
        dto.setAddress(household.getAddress());
        dto.setPhone(household.getPhone());
        dto.setMemberCount(household.getMemberCount());
        return dto;
    }

    private com.village.dto.ResidentDTO toResidentDTO(Resident resident) {
        com.village.dto.ResidentDTO dto = new com.village.dto.ResidentDTO();
        dto.setId(resident.getId());
        dto.setName(resident.getName());
        dto.setIdCard(resident.getIdCard());
        dto.setGender(resident.getGender());
        dto.setBirthDate(resident.getBirthDate());
        dto.setPhone(resident.getPhone());
        dto.setAddress(resident.getAddress());
        dto.setRelationship(resident.getRelationship());
        return dto;
    }

    private HouseholdIncomeDTO toIncomeDTO(HouseholdIncome income) {
        HouseholdIncomeDTO dto = new HouseholdIncomeDTO();
        dto.setId(income.getId());
        dto.setHouseholdId(income.getHouseholdId());
        dto.setYear(income.getYear());
        dto.setTotalIncome(income.getTotalIncome());
        dto.setPerCapitaIncome(income.getPerCapitaIncome());
        dto.setIncomeSource(income.getIncomeSource());
        dto.setRemark(income.getRemark());
        return dto;
    }

    private com.village.dto.HouseholdChangeDTO toChangeDTO(com.village.entity.HouseholdChange change) {
        com.village.dto.HouseholdChangeDTO dto = new com.village.dto.HouseholdChangeDTO();
        dto.setId(change.getId());
        dto.setHouseholdId(change.getHouseholdId());
        dto.setChangeType(change.getChangeType());
        dto.setChangeTime(change.getChangeTime());
        dto.setChangeReason(change.getChangeReason());
        dto.setRelatedPersons(change.getRelatedPersons());
        dto.setBeforeStatus(change.getBeforeStatus());
        dto.setAfterStatus(change.getAfterStatus());
        dto.setRemark(change.getRemark());
        return dto;
    }
}