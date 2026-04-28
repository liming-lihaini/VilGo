package com.village.controller;

import com.village.dto.NoticeDTO;
import com.village.dto.NoticeFeedbackDTO;
import com.village.dto.NoticeFeedbackReplyDTO;
import com.village.dto.NoticeQueryDTO;
import com.village.dto.Result;
import com.village.entity.Notice;
import com.village.entity.NoticeFeedback;
import com.village.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 公示控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;

    @PostMapping("/create")
    public Result<Notice> create(@Valid @RequestBody NoticeDTO dto) {
        log.info("创建公示，title={}", dto.getTitle());
        Notice notice = noticeService.create(dto);
        return Result.success(notice);
    }

    @PutMapping("/update")
    public Result<Notice> update(@Valid @RequestBody NoticeDTO dto) {
        log.info("更新公示，id={}", dto.getId());
        Notice notice = noticeService.update(dto);
        return Result.success(notice);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除公示，id={}", id);
        noticeService.delete(id);
        return Result.success();
    }

    @GetMapping("/get/{id}")
    public Result<Notice> get(@PathVariable Long id) {
        log.info("获取公示详情，id={}", id);
        Notice notice = noticeService.getById(id);
        return Result.success(notice);
    }

    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody NoticeQueryDTO dto) {
        log.info("查询公示列表");
        List<Notice> list = noticeService.list(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Result.success(result);
    }

    @PostMapping("/feedback")
    public Result<NoticeFeedback> submitFeedback(@Valid @RequestBody NoticeFeedbackDTO dto) {
        log.info("提交公示反馈，noticeId={}", dto.getNoticeId());
        NoticeFeedback feedback = noticeService.submitFeedback(dto);
        return Result.success(feedback);
    }

    @PutMapping("/feedback/reply")
    public Result<Void> replyFeedback(@Valid @RequestBody NoticeFeedbackReplyDTO dto) {
        log.info("回复公示反馈，id={}", dto.getId());
        noticeService.replyFeedback(dto);
        return Result.success();
    }

    @GetMapping("/feedback/list/{noticeId}")
    public Result<List<NoticeFeedback>> getFeedbackList(@PathVariable Long noticeId) {
        log.info("获取公示反馈列表，noticeId={}", noticeId);
        List<NoticeFeedback> list = noticeService.getFeedbackList(noticeId);
        return Result.success(list);
    }
}
