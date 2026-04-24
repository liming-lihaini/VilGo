package com.village.service;

import com.village.dto.MeetingDTO;
import com.village.dto.MeetingQueryDTO;
import com.village.entity.Meeting;

import java.util.List;

/**
 * 会议服务接口
 */
public interface MeetingService {

    Meeting create(MeetingDTO dto);

    Meeting update(MeetingDTO dto);

    void delete(Long id);

    Meeting getById(Long id);

    List<Meeting> list(MeetingQueryDTO dto);
}
