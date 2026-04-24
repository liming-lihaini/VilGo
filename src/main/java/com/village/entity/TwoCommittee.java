package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 两委班子成员实体类
 * 对应数据库表 two_committee
 */
@Data
@TableName("two_committee")
public class TwoCommittee {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long residentId;

    private String name;

    private String gender;

    private String positionIds;

    private String positionNames;

    private String phone;

    private String dividedWork;

    private String joinDate;

    private String creator;

    private String createTime;

    private String updateTime;

    @TableLogic
    private Integer deleted;
}
