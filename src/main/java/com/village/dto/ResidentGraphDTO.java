package com.village.dto;

import lombok.Data;
import java.util.List;

/**
 * 村民知识图谱数据 DTO
 */
@Data
public class ResidentGraphDTO {

    /**
     * 村民基本信息
     */
    private ResidentDTO resident;

    /**
     * 党员档案信息（如果有）
     */
    private PartyMemberDTO partyMember;

    /**
     * 特殊人群记录
     */
    private List<SpecialGroupDTO> specialGroups;

    /**
     * 户籍成员关系
     */
    private List<HouseholdMemberDTO> householdMembers;

    /**
     * 公益活动参与记录
     */
    private List<ActivitySignupDTO> activities;

    /**
     * 党费缴纳记录
     */
    private List<PartyDuesDTO> partyDues;

    /**
     * 党务活动参与记录
     */
    private List<PartyActivityDTO> partyActivities;
}