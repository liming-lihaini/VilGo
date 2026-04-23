package com.village.service;

import com.village.dto.HouseholdMemberDTO;
import com.village.entity.HouseholdMember;
import com.village.entity.Resident;
import java.util.List;

/**
 * 家庭成员服务接口
 */
public interface HouseholdMemberService {

    /**
     * 添加家庭成员
     */
    HouseholdMember addMember(HouseholdMemberDTO dto);

    /**
     * 移除家庭成员（软删除）
     */
    void removeMember(Long id);

    /**
     * 获取家庭成员列表（包含村民详细信息）
     */
    List<Resident> getMembersByHouseholdId(Long householdId);

    /**
     * 批量添加家庭成员
     */
    void batchAddMembers(Long householdId, List<Long> residentIds);
}