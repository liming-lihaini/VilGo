package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 党费收缴实体类
 * 对应数据库表 party_dues
 */
@Data
@TableName("party_dues")
public class PartyDues {

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
     * 缴费金额
     */
    private BigDecimal amount;

    /**
     * 缴费月份（格式：yyyy-MM）
     */
    private String payMonth;

    /**
     * 缴费日期
     */
    private String payDate;

    /**
     * 缴费状态：unpaid-未缴，paid-已缴
     */
    private String status;

    /**
     * 备注
     */
    private String remark;

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
     * 软删除标记：0-正常，1-已删除
     */
    @TableLogic
    private Integer deleted;
}