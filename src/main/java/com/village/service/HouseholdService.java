package com.village.service;

import com.village.dto.HouseholdDTO;
import com.village.dto.HouseholdDetailDTO;
import com.village.dto.HouseholdQueryDTO;
import com.village.entity.Household;
import java.util.List;

/**
 * 家庭户服务接口
 */
public interface HouseholdService {

    /**
     * 新增家庭户
     */
    Household create(HouseholdDTO dto);

    /**
     * 更新家庭户
     */
    Household update(HouseholdDTO dto);

    /**
     * 删除家庭户（软删除）
     */
    void delete(Long id);

    /**
     * 获取家庭户详情（包含成员、收入、变动记录）
     */
    HouseholdDetailDTO getDetail(Long id);

    /**
     * 查询家庭户列表
     */
    List<Household> list(HouseholdQueryDTO dto);

    /**
     * 从村民档案同步户主（创建家庭户）
     */
    Household syncFromResident(Long residentId);

    /**
     * 同步所有户主
     */
    void syncAllFromResidents();
}