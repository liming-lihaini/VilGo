package com.village.dto;

import lombok.Data;
import java.time.LocalDate;

/**
 * 村民档案查询条件 DTO
 */
@Data
public class ResidentQueryDTO {

    /**
     * 姓名（模糊查询）
     */
    private String name;

    /**
     * 身份证号（模糊查询）
     */
    private String idCard;

    /**
     * 性别
     */
    private String gender;

    /**
     * 户籍状态：normal-正常，cancelled-已销户
     */
    private String householdStatus;

    /**
     * 人员类型
     */
    private String personType;

    /**
     * 保障类型
     */
    private String securityType;

    /**
     * 所属村组
     */
    private String village;

    /**
     * 最小出生日期
     */
    private LocalDate birthDateStart;

    /**
     * 最大出生日期
     */
    private LocalDate birthDateEnd;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;

    /**
     * 排序字段
     */
    private String orderBy = "create_time";

    /**
     * 排序方向：asc/desc
     */
    private String sortOrder = "desc";
}