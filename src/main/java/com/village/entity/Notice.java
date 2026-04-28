package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 公示实体类
 * 对应数据库表 notices
 */
@Data
@TableName("notices")
public class Notice {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 公示标题
     */
    private String title;

    /**
     * 公示内容
     */
    private String content;

    /**
     * 公示类型：通知/政策文件/财务公开/项目公示/惠民信息
     */
    private String noticeType;

    /**
     * 发布人
     */
    private String publisher;

    /**
     * 公示期限
     */
    private String expireDate;

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
     * 删除标记（0-未删除，1-已删除）
     */
    @TableLogic
    private Integer deleted;
}
