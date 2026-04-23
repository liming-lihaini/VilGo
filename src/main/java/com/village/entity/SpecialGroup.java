package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 特殊人群实体类
 * 对应数据库表 special_groups
 */
@Data
@TableName("special_groups")
public class SpecialGroup {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long residentId;

    private String idCard;

    private String name;

    private String gender;

    private String birthDate;

    private String phone;

    private String address;

    private String groupType;

    private Long helperId;

    private String helperName;

    private String helpContent;

    private String helpTime;

    private String helpResult;

    private String helpStatus;

    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private String updateTime;

    @TableLogic
    private Integer deleted;
}