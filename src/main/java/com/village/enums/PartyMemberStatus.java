package com.village.enums;

/**
 * 党员状态枚举
 * 党员状态流转顺序：积极分子 → 发展对象 → 预备党员 → 正式党员
 * 顺序不可逆
 */
public enum PartyMemberStatus {

    /**
     * 积极分子
     */
    ACTIVIST("activist", "积极分子"),

    /**
     * 发展对象
     */
    DEVELOPING("developing", "发展对象"),

    /**
     * 预备党员
     */
    PROBATIONARY("probationary", "预备党员"),

    /**
     * 正式党员
     */
    FORMAL("formal", "正式党员");

    private final String code;
    private final String desc;

    PartyMemberStatus(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    /**
     * 根据 code 获取枚举
     */
    public static PartyMemberStatus fromCode(String code) {
        for (PartyMemberStatus status : values()) {
            if (status.code.equals(code)) {
                return status;
            }
        }
        return null;
    }

    /**
     * 检查是否可以流转到目标状态
     * 状态流转顺序：activist → developing → probationary → formal
     * 只能向前流转，不能后退
     */
    public static boolean canTransition(PartyMemberStatus from, PartyMemberStatus to) {
        if (from == null || to == null) {
            return false;
        }
        return from.ordinal() < to.ordinal();
    }

    /**
     * 检查目标状态是否允许入党时间设置
     */
    public static boolean requiresJoinDate(PartyMemberStatus status) {
        return status == ACTIVIST || status == DEVELOPING;
    }

    /**
     * 检查目标状态是否需要转正时间
     */
    public static boolean requiresConvertDate(PartyMemberStatus status) {
        return status == PROBATIONARY || status == FORMAL;
    }
}