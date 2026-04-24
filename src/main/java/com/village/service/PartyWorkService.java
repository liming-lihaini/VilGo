package com.village.service;

import com.village.dto.PartyActivityDTO;
import com.village.dto.PartyDuesDTO;
import com.village.dto.PartyDuesQueryDTO;
import com.village.dto.PartyMemberDTO;
import com.village.dto.PartyMemberQueryDTO;
import com.village.dto.PartyMemberOutputDTO;
import com.village.entity.PartyActivity;
import com.village.entity.PartyDues;
import com.village.entity.PartyMember;
import java.util.List;

/**
 * 党务管理服务接口
 */
public interface PartyWorkService {

    // ==================== 党员档案相关 ====================

    /**
     * 新增党员档案
     */
    PartyMember create(PartyMemberDTO dto);

    /**
     * 更新党员档案
     */
    PartyMember update(PartyMemberDTO dto);

    /**
     * 删除党员档案（软删除）
     */
    void delete(Long id);

    /**
     * 获取党员档案
     */
    PartyMember getById(Long id);

    /**
     * 查询党员列表
     */
    List<PartyMemberOutputDTO> list(PartyMemberQueryDTO dto);

    /**
     * 更新党员状态（状态流转）
     */
    PartyMember updateStatus(Long id, String newStatus);

    // ==================== 党务活动相关 ====================

    /**
     * 新增党务活动
     */
    PartyActivity createActivity(PartyActivityDTO dto);

    /**
     * 更新党务活动
     */
    PartyActivity updateActivity(PartyActivityDTO dto);

    /**
     * 删除党务活动（软删除）
     */
    void deleteActivity(Long id);

    /**
     * 获取党务活动
     */
    PartyActivity getActivityById(Long id);

    /**
     * 查询党务活动列表
     */
    List<PartyActivity> listActivities(PartyActivityDTO dto);

    // ==================== 党费收缴相关 ====================

    /**
     * 新增党费记录
     */
    PartyDues createDues(PartyDuesDTO dto);

    /**
     * 更新党费记录
     */
    PartyDues updateDues(PartyDuesDTO dto);

    /**
     * 删除党费记录（软删除）
     */
    void deleteDues(Long id);

    /**
     * 获取党费记录
     */
    PartyDues getDuesById(Long id);

    /**
     * 查询党费列表
     */
    List<PartyDues> listDues(PartyDuesQueryDTO dto);

    /**
     * 标记党费为已缴
     */
    PartyDues markAsPaid(Long id);

    // ==================== 统计相关 ====================

    /**
     * 党员统计
     */
    Object partyMemberStatistics();

    /**
     * 党费收缴统计
     */
    Object partyDuesStatistics();
}