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