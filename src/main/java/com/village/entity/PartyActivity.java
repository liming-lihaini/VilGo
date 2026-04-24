package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 党务活动实体类
 * 对应数据库表 party_activities
 */
@Data
@TableName("party_activities")
public class PartyActivity {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 党员ID
     */
    private Long memberId;

    /**
     * 活动主题
     */
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
     * 参与人员（JSON数组）
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

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 更新时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    /**
     * 软删除标记：0-正常，1-已删除
     */
    @TableLogic
    private Integer deleted;
}