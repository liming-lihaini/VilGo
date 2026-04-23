package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.village.dao.HouseholdDao;
import com.village.dao.HouseholdMemberDao;
import com.village.dao.ResidentDao;
import com.village.dto.HouseholdMemberDTO;
import com.village.dto.HouseholdMemberDetailDTO;
import com.village.entity.Household;
import com.village.entity.HouseholdMember;
import com.village.entity.Resident;
import com.village.exception.BusinessException;
import com.village.service.HouseholdMemberService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 家庭成员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HouseholdMemberServiceImpl implements HouseholdMemberService {

    private final HouseholdMemberDao householdMemberDao;
    private final HouseholdDao householdDao;
    private final ResidentDao residentDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HouseholdMember addMember(HouseholdMemberDTO dto) {
        if (dto.getHouseholdId() == null) {
            throw new BusinessException("家庭户ID不能为空");
        }

        if (dto.getResidentId() == null) {
            throw new BusinessException("村民ID不能为空");
        }
        Resident resident = residentDao.selectById(dto.getResidentId());
        if (resident == null) {
            throw new BusinessException("村民档案不存在");
        }

        int count = householdMemberDao.countByResidentIdExcludeHousehold(dto.getResidentId(), dto.getHouseholdId());
        if (count > 0) {
            throw new BusinessException("该村民已是其他家庭户成员");
        }

        HouseholdMember member = new HouseholdMember();
        member.setHouseholdId(dto.getHouseholdId());
        member.setResidentId(dto.getResidentId());
        member.setRelation(dto.getRelation());
        member.setCreateTime(DateUtils.now());
        member.setDeleted(0);

        householdMemberDao.insert(member);

        // 更新家庭户成员数量
        updateMemberCount(dto.getHouseholdId());

        log.info("添加家庭成员成功，id={}, householdId={}, residentId={}", member.getId(), member.getHouseholdId(), member.getResidentId());
        return member;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removeMember(Long id) {
        HouseholdMember member = householdMemberDao.selectById(id);
        if (member == null) {
            throw new BusinessException("记录不存在");
        }
        Long householdId = member.getHouseholdId();

        // 标记删除
        householdMemberDao.deleteById(id);

        // 更新家庭户成员数量
        updateMemberCount(householdId);

        log.info("移除家庭成员成功，id={}", id);
    }

    /**
     * 更新家庭户成员数量
     */
    private void updateMemberCount(Long householdId) {
        if (householdId == null) return;

        LambdaQueryWrapper<HouseholdMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(HouseholdMember::getHouseholdId, householdId)
               .eq(HouseholdMember::getDeleted, 0);
        long count = householdMemberDao.selectCount(wrapper);

        LambdaQueryWrapper<Household> householdWrapper = new LambdaQueryWrapper<>();
        householdWrapper.eq(Household::getId, householdId);
        Household household = new Household();
        household.setMemberCount((int) count);
        householdDao.update(household, householdWrapper);
    }

    @Override
    public List<HouseholdMemberDetailDTO> getMembersByHouseholdId(Long householdId) {
        List<HouseholdMember> memberRelations = householdMemberDao.selectByHouseholdId(householdId);
        if (memberRelations.isEmpty()) {
            return List.of();
        }

        // 查询成员详细信息
        List<Long> residentIds = memberRelations.stream()
                .map(HouseholdMember::getResidentId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Resident::getId, residentIds);
        wrapper.eq(Resident::getDeleted, 0);

        List<Resident> residents = residentDao.selectList(wrapper);

        // 构建 residentId -> Resident 映射
        Map<Long, Resident> residentMap = residents.stream()
                .collect(Collectors.toMap(Resident::getId, r -> r));

        // 组装结果，包含关系信息
        return memberRelations.stream()
                .map(m -> {
                    HouseholdMemberDetailDTO dto = new HouseholdMemberDetailDTO();
                    dto.setId(m.getId());
                    dto.setResidentId(m.getResidentId());
                    dto.setRelation(m.getRelation());

                    Resident r = residentMap.get(m.getResidentId());
                    if (r != null) {
                        dto.setName(r.getName());
                        dto.setIdCard(r.getIdCard());
                        dto.setGender(r.getGender());
                        dto.setPhone(r.getPhone());
                        dto.setAddress(r.getAddress());
                    }
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchAddMembers(Long householdId, List<Long> residentIds) {
        for (Long residentId : residentIds) {
            HouseholdMemberDTO dto = new HouseholdMemberDTO();
            dto.setHouseholdId(householdId);
            dto.setResidentId(residentId);
            addMember(dto);
        }
    }
}