package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.village.dao.NoticeDao;
import com.village.dao.NoticeFeedbackDao;
import com.village.dto.NoticeDTO;
import com.village.dto.NoticeFeedbackDTO;
import com.village.dto.NoticeFeedbackReplyDTO;
import com.village.dto.NoticeQueryDTO;
import com.village.entity.Notice;
import com.village.entity.NoticeFeedback;
import com.village.exception.BusinessException;
import com.village.service.NoticeService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 公示服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NoticeServiceImpl implements NoticeService {

    private final NoticeDao noticeDao;
    private final NoticeFeedbackDao noticeFeedbackDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notice create(NoticeDTO dto) {
        // 标题非空校验
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new BusinessException("公示标题不能为空");
        }

        // 内容非空校验
        if (!StringUtils.hasText(dto.getContent())) {
            throw new BusinessException("公示内容不能为空");
        }

        // 标题唯一性校验
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getTitle, dto.getTitle());
        Long count = noticeDao.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("已存在同名公示");
        }

        Notice notice = new Notice();
        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setNoticeType(dto.getNoticeType());
        notice.setPublisher(dto.getPublisher());
        notice.setExpireDate(dto.getExpireDate());
        notice.setCreator(dto.getCreator());
        notice.setCreateTime(DateUtils.now());
        notice.setUpdateTime(DateUtils.now());
        notice.setDeleted(0);

        noticeDao.insert(notice);
        log.info("创建公示成功，id={}, title={}", notice.getId(), notice.getTitle());
        return notice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Notice update(NoticeDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("公示ID不能为空");
        }

        Notice notice = noticeDao.selectById(dto.getId());
        if (notice == null || notice.getDeleted() == 1) {
            throw new BusinessException("公示不存在");
        }

        // 标题非空校验
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new BusinessException("公示标题不能为空");
        }

        // 内容非空校验
        if (!StringUtils.hasText(dto.getContent())) {
            throw new BusinessException("公示内容不能为空");
        }

        // 标题唯一性校验（排除自身）
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Notice::getTitle, dto.getTitle());
        wrapper.ne(Notice::getId, dto.getId());
        Long count = noticeDao.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("已存在同名公示");
        }

        notice.setTitle(dto.getTitle());
        notice.setContent(dto.getContent());
        notice.setNoticeType(dto.getNoticeType());
        notice.setPublisher(dto.getPublisher());
        notice.setExpireDate(dto.getExpireDate());
        notice.setUpdateTime(DateUtils.now());

        noticeDao.updateById(notice);
        log.info("更新公示成功，id={}, title={}", notice.getId(), notice.getTitle());
        return notice;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Notice notice = noticeDao.selectById(id);
        if (notice == null || notice.getDeleted() == 1) {
            throw new BusinessException("公示不存在");
        }

        noticeDao.deleteById(id);
        log.info("删除公示成功，id={}, title={}", id, notice.getTitle());
    }

    @Override
    public Notice getById(Long id) {
        Notice notice = noticeDao.selectById(id);
        if (notice == null || notice.getDeleted() == 1) {
            throw new BusinessException("公示不存在");
        }
        return notice;
    }

    @Override
    public List<Notice> list(NoticeQueryDTO dto) {
        LambdaQueryWrapper<Notice> wrapper = new LambdaQueryWrapper<>();

        if (StringUtils.hasText(dto.getTitle())) {
            wrapper.like(Notice::getTitle, dto.getTitle());
        }

        if (StringUtils.hasText(dto.getNoticeType())) {
            wrapper.eq(Notice::getNoticeType, dto.getNoticeType());
        }

        if (StringUtils.hasText(dto.getStartTime())) {
            wrapper.ge(Notice::getCreateTime, dto.getStartTime());
        }

        if (StringUtils.hasText(dto.getEndTime())) {
            wrapper.le(Notice::getCreateTime, dto.getEndTime());
        }

        wrapper.orderByDesc(Notice::getCreateTime);

        return noticeDao.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public NoticeFeedback submitFeedback(NoticeFeedbackDTO dto) {
        if (dto.getNoticeId() == null) {
            throw new BusinessException("公示ID不能为空");
        }

        Notice notice = noticeDao.selectById(dto.getNoticeId());
        if (notice == null || notice.getDeleted() == 1) {
            throw new BusinessException("公示不存在");
        }

        if (!StringUtils.hasText(dto.getContent())) {
            throw new BusinessException("反馈内容不能为空");
        }

        NoticeFeedback feedback = new NoticeFeedback();
        feedback.setNoticeId(dto.getNoticeId());
        feedback.setResidentId(dto.getResidentId());
        feedback.setContent(dto.getContent());
        feedback.setCreateTime(DateUtils.now());
        feedback.setDeleted(0);

        noticeFeedbackDao.insert(feedback);
        log.info("提交公示反馈成功，id={}, noticeId={}", feedback.getId(), dto.getNoticeId());
        return feedback;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void replyFeedback(NoticeFeedbackReplyDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("反馈ID不能为空");
        }

        NoticeFeedback feedback = noticeFeedbackDao.selectById(dto.getId());
        if (feedback == null || feedback.getDeleted() == 1) {
            throw new BusinessException("反馈不存在");
        }

        if (!StringUtils.hasText(dto.getReply())) {
            throw new BusinessException("回复内容不能为空");
        }

        feedback.setReply(dto.getReply());
        noticeFeedbackDao.updateById(feedback);
        log.info("回复公示反馈成功，id={}", dto.getId());
    }

    @Override
    public List<NoticeFeedback> getFeedbackList(Long noticeId) {
        if (noticeId == null) {
            throw new BusinessException("公示ID不能为空");
        }

        LambdaQueryWrapper<NoticeFeedback> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(NoticeFeedback::getNoticeId, noticeId);
        wrapper.orderByDesc(NoticeFeedback::getCreateTime);

        return noticeFeedbackDao.selectList(wrapper);
    }
}
