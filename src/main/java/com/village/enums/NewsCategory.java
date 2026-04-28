package com.village.enums;

/**
 * 新闻分类枚举
 * 预置4种分类：政策解读、活动报道、先进事迹、通知公告
 */
public enum NewsCategory {

    /**
     * 政策解读
     */
    POLICY_INTERPRETATION("policy_interpretation", "政策解读"),

    /**
     * 活动报道
     */
    ACTIVITY_REPORT("activity_report", "活动报道"),

    /**
     * 先进事迹
     */
    ADVANCED_DEEDS("advanced_deeds", "先进事迹"),

    /**
     * 通知公告
     */
    NOTICE_ANNOUNCEMENT("notice_announcement", "通知公告");

    private final String code;
    private final String displayName;

    NewsCategory(String code, String displayName) {
        this.code = code;
        this.displayName = displayName;
    }

    public String getCode() {
        return code;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * 根据 code 获取枚举
     */
    public static NewsCategory fromCode(String code) {
        for (NewsCategory category : values()) {
            if (category.code.equals(code)) {
                return category;
            }
        }
        return null;
    }

    /**
     * 校验分类是否有效
     */
    public static boolean isValid(String code) {
        return fromCode(code) != null;
    }
}
