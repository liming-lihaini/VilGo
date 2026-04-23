package com.village.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 户籍变动入参 DTO
 */
@Data
public class HouseholdChangeDTO {

    /**
     * 主键ID（编辑时使用）
     */
    private Long id;

    /**
     * 家庭户ID
     */
    private Long householdId;

    /**
     * 变动类型：move_in-迁入，move_out-迁出，newborn-新生儿，split-分户，merge-合户
     */
    @NotBlank(message = "变动类型不能为空")
    private String changeType;

    /**
     * 变动时间
     */
    private String changeTime;

    /**
     * 变动原因
     */
    private String changeReason;

    /**
     * 相关人员（多人用逗号分隔）
     */
    private String relatedPersons;

    /**
     * 变动前状态
     */
    private String beforeStatus;

    /**
     * 变动后状态
     */
    private String afterStatus;

    /**
     * 备注
     */
    private String remark;
}