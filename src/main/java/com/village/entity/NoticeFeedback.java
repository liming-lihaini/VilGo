package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 公示反馈实体类
 * 对应数据库表 notice_feedback
 */
@Data
@TableName("notice_feedback")
public class NoticeFeedback {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公示ID
     */
    private Long noticeId;

    /**
     * 反馈人ID（村民ID）
     */
    private Long residentId;

    /**
     * 反馈内容
     */
    private String content;

    /**
     * 回复内容
     */
    private String reply;

    /**
     * 创建时间
     */
    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    /**
     * 删除标记（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer deleted;
}
