package com.village.controller;

import com.village.dto.*;
import com.village.entity.Meeting;
import com.village.entity.Task;
import com.village.service.MeetingService;
import com.village.service.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/meeting")
@RequiredArgsConstructor
public class MeetingController {

    private final MeetingService meetingService;
    private final TaskService taskService;

    @PostMapping("/create")
    public Result<Meeting> create(@Valid @RequestBody MeetingDTO dto) {
        log.info("创建会议记录");
        Meeting meeting = meetingService.create(dto);
        return Result.success(meeting);
    }

    @PutMapping("/update")
    public Result<Meeting> update(@Valid @RequestBody MeetingDTO dto) {
        log.info("更新会议记录，id={}", dto.getId());
        Meeting meeting = meetingService.update(dto);
        return Result.success(meeting);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除会议记录，id={}", id);
        meetingService.delete(id);
        return Result.success();
    }

    @GetMapping("/get/{id}")
    public Result<Meeting> get(@PathVariable Long id) {
        log.info("获取会议记录详情，id={}", id);
        Meeting meeting = meetingService.getById(id);
        return Result.success(meeting);
    }

    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody MeetingQueryDTO dto) {
        log.info("查询会议记录列表");
        List<Meeting> list = meetingService.list(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Result.success(result);
    }

    @PostMapping("/convert")
    public Result<Task> convertToTask(@RequestBody TaskDTO dto) {
        log.info("决议关联任务，meetingId={}", dto.getContent());
        Task task = taskService.create(dto);
        return Result.success(task);
    }
}
