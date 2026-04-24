package com.village.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 党务活动新增/编辑入参 DTO
 */
@Data
public class PartyActivityDTO {

    /**
     * 主键ID（编辑时使用）
     */
    private Long id;

    /**
     * 党员ID
     */
    @NotNull(message = "党员ID不能为空")
    private Long memberId;

    /**
     * 活动主题
     */
    @NotBlank(message = "活动主题不能为空")
    private String theme;

    /**
     * 活动类型：theme_day-主题党日，meeting-组织生活会，study-学习培训，volunteer-志愿服务，other-其他
     */
    private String activityType;

    /**
     * 活动时间
     */
    private String activityTime;

    /**
     * 活动地点
     */
    private String location;

    /**
     * 参与人员（JSON数组字符串）
     */
    private String participation;

    /**
     * 活动内容
     */
    private String content;

    /**
     * 创建人
     */
    private String creator;
}