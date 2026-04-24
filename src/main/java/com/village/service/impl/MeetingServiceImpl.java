package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.village.dao.MeetingDao;
import com.village.dto.MeetingDTO;
import com.village.dto.MeetingQueryDTO;
import com.village.entity.Meeting;
import com.village.exception.BusinessException;
import com.village.service.MeetingService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MeetingServiceImpl implements MeetingService {

    private final MeetingDao meetingDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Meeting create(MeetingDTO dto) {
        Meeting meeting = new Meeting();
        meeting.setMeetingTime(dto.getMeetingTime());
        meeting.setLocation(dto.getLocation());
        meeting.setAttendees(dto.getAttendees());
        meeting.setContent(dto.getContent());
        meeting.setResolution(dto.getResolution());
        meeting.setImplementation(dto.getImplementation());
        meeting.setCreator(dto.getCreator());
        meeting.setCreateTime(DateUtils.now());
        meeting.setDeleted(0);

        meetingDao.insert(meeting);
        log.info("新增会议记录成功，id={}", meeting.getId());
        return meeting;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Meeting update(MeetingDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("会议ID不能为空");
        }

        Meeting meeting = meetingDao.selectById(dto.getId());
        if (meeting == null || meeting.getDeleted() == 1) {
            throw new BusinessException("会议记录不存在");
        }

        meeting.setMeetingTime(dto.getMeetingTime());
        meeting.setLocation(dto.getLocation());
        meeting.setAttendees(dto.getAttendees());
        meeting.setContent(dto.getContent());
        meeting.setResolution(dto.getResolution());
        meeting.setImplementation(dto.getImplementation());

        meetingDao.updateById(meeting);
        log.info("更新会议记录成功，id={}", dto.getId());
        return meeting;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Meeting meeting = meetingDao.selectById(id);
        if (meeting == null || meeting.getDeleted() == 1) {
            throw new BusinessException("会议记录不存在");
        }

        meeting.setDeleted(1);
        meetingDao.updateById(meeting);
        log.info("删除会议记录成功，id={}", id);
    }

    @Override
    public Meeting getById(Long id) {
        return meetingDao.selectById(id);
    }

    @Override
    public List<Meeting> list(MeetingQueryDTO dto) {
        LambdaQueryWrapper<Meeting> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Meeting::getDeleted, 0);

        if (StringUtils.hasText(dto.getMeetingTime())) {
            wrapper.like(Meeting::getMeetingTime, dto.getMeetingTime());
        }

        wrapper.orderByDesc(Meeting::getMeetingTime);

        Page<Meeting> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<Meeting> result = meetingDao.selectPage(page, wrapper);
        return result.getRecords();
    }
}
