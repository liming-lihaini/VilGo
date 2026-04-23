package com.village.dto;

import lombok.Data;

/**
 * 家庭户查询入参 DTO
 */
@Data
public class HouseholdQueryDTO {

    /**
     * 家庭户编号（模糊查询）
     */
    private String householdNo;

    /**
     * 户主姓名（模糊查询）
     */
    private String headName;

    /**
     * 户主身份证号（模糊查询）
     */
    private String headIdCard;

    /**
     * 住址（模糊查询）
     */
    private String address;

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页数量
     */
    private Integer pageSize = 10;
}