package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.village.dao.HouseholdMemberDao;
import com.village.dao.ResidentDao;
import com.village.dto.HouseholdMemberDTO;
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

/**
 * 家庭成员服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HouseholdMemberServiceImpl implements HouseholdMemberService {

    private final HouseholdMemberDao householdMemberDao;
    private final ResidentDao residentDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HouseholdMember addMember(HouseholdMemberDTO dto) {
        // 校验家庭户存在
        if (dto.getHouseholdId() == null) {
            throw new BusinessException("家庭户ID不能为空");
        }

        // 校验村民存在
        if (dto.getResidentId() == null) {
            throw new BusinessException("村民ID不能为空");
        }
        Resident resident = residentDao.selectById(dto.getResidentId());
        if (resident == null) {
            throw new BusinessException("村民档案不存在");
        }

        // 校验该村民是否已是其他家庭户成员
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

        member.setDeleted(1);
        householdMemberDao.updateById(member);
        log.info("移除家庭成员成功，id={}", id);
    }

    @Override
    public List<Resident> getMembersByHouseholdId(Long householdId) {
        List<HouseholdMember> members = householdMemberDao.selectByHouseholdId(householdId);
        if (members.isEmpty()) {
            return List.of();
        }

        // 查询成员详细信息
        List<Long> residentIds = members.stream()
                .map(HouseholdMember::getResidentId)
                .collect(java.util.stream.Collectors.toList());

        LambdaQueryWrapper<Resident> wrapper = new LambdaQueryWrapper<>();
        wrapper.in(Resident::getId, residentIds);
        wrapper.eq(Resident::getDeleted, 0);

        return residentDao.selectList(wrapper);
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