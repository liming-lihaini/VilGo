package com.village.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

/**
 * 村民人口统计结果 DTO
 */
@Data
public class ResidentStatisticsDTO {

    /**
     * 总人口数
     */
    private Long totalCount;

    /**
     * 常住人口数
     */
    private Long localResidentCount;

    /**
     * 就读学生数
     */
    private Long studentCount;

    /**
     * 低保人数
     */
    private Long allowanceCount;

    /**
     * 五保户人数
     */
    private Long fiveGuaranteeCount;

    /**
     * 非本地户籍人数
     */
    private Long nonLocalHouseholdCount;

    /**
     * 按户籍状态统计
     */
    private Map<String, Long> householdStatusCount;

    /**
     * 按人员类型统计
     */
    private Map<String, Long> personTypeCount;

    /**
     * 按保障类型统计
     */
    private Map<String, Long> securityTypeCount;

    /**
     * 按村组统计
     */
    private Map<String, Long> villageCount;

    /**
     * 明细列表（用于图表展示）
     */
    private List<StatisticsItem> details;

    /**
     * 统计项
     */
    @Data
    public static class StatisticsItem {
        private String name;
        private Long value;
    }
}