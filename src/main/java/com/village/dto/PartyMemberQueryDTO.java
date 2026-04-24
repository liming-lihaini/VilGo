package com.village.dto;

import lombok.Data;

/**
 * 党员档案查询条件 DTO
 */
@Data
public class PartyMemberQueryDTO {

    /**
     * 姓名（模糊查询）
     */
    private String name;

    /**
     * 身份证号（模糊查询）
     */
    private String idCard;

    /**
     * 党员状态
     */
    private String status;

    /**
     * 性别
     */
    private String gender;

    /**
     * 是否本村户籍：0-否，1-是
     */
    private Integer isLocalHousehold;

    /**
     * 页码
     */
    private Integer pageNum = 1;

    /**
     * 每页条数
     */
    private Integer pageSize = 10;
}