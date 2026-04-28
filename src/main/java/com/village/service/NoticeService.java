package com.village.service;

import com.village.dto.NoticeDTO;
import com.village.dto.NoticeFeedbackDTO;
import com.village.dto.NoticeFeedbackReplyDTO;
import com.village.dto.NoticeQueryDTO;
import com.village.entity.Notice;
import com.village.entity.NoticeFeedback;

import java.util.List;

/**
 * 公示服务接口
 */
public interface NoticeService {

    /**
     * 创建公示
     */
    Notice create(NoticeDTO dto);

    /**
     * 更新公示
     */
    Notice update(NoticeDTO dto);

    /**
     * 删除公示
     */
    void delete(Long id);

    /**
     * 根据ID获取公示
     */
    Notice getById(Long id);

    /**
     * 查询公示列表
     */
    List<Notice> list(NoticeQueryDTO dto);

    /**
     * 提交反馈
     */
    NoticeFeedback submitFeedback(NoticeFeedbackDTO dto);

    /**
     * 回复反馈
     */
    void replyFeedback(NoticeFeedbackReplyDTO dto);

    /**
     * 获取公示的反馈列表
     */
    List<NoticeFeedback> getFeedbackList(Long noticeId);
}
