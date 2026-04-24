package com.village.dto;

import lombok.Data;

/**
 * 会议查询 DTO
 */
@Data
public class MeetingQueryDTO {

    private String meetingTime;

    private Integer pageNum = 1;

    private Integer pageSize = 10;
}
