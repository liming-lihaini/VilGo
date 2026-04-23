package com.village.service;

import com.village.dto.HouseholdChangeDTO;
import com.village.entity.HouseholdChange;
import java.util.List;

/**
 * 户籍变动服务接口
 */
public interface HouseholdChangeService {

    /**
     * 登记户籍变动
     */
    HouseholdChange createChange(HouseholdChangeDTO dto);

    /**
     * 删除户籍变动记录（软删除）
     */
    void deleteChange(Long id);

    /**
     * 查询家庭户籍变动记录列表
     */
    List<HouseholdChange> listByHouseholdId(Long householdId);
}