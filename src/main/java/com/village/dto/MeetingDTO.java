package com.village.dto;

import lombok.Data;

/**
 * 会议新增/更新 DTO
 */
@Data
public class MeetingDTO {

    private Long id;

    private String meetingTime;

    private String location;

    private String attendees;

    private String content;

    private String resolution;

    private String implementation;

    private String creator;
}
