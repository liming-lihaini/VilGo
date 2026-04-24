package com.village.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

/**
 * 会议记录实体类
 * 对应数据库表 meetings
 */
@Data
@TableName("meetings")
public class Meeting {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String meetingTime;

    private String location;

    private String attendees;

    private String content;

    private String resolution;

    private String implementation;

    private String creator;

    @TableField(fill = FieldFill.INSERT)
    private String createTime;

    @TableLogic
    private Integer deleted;
}
