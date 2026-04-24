package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 职位实体类
 * 对应数据库表 positions
 */
@Data
@TableName("positions")
public class Position {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String code;

    private Integer sortOrder;

    private String createTime;

    private String updateTime;

    @TableLogic
    private Integer deleted;
}
