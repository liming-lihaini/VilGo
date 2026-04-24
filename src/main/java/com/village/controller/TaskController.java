package com.village.controller;

import com.village.dto.*;
import com.village.entity.Task;
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
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/create")
    public Result<Task> create(@Valid @RequestBody TaskDTO dto) {
        log.info("创建任务，content={}", dto.getContent());
        Task task = taskService.create(dto);
        return Result.success(task);
    }

    @PutMapping("/update")
    public Result<Task> update(@Valid @RequestBody TaskDTO dto) {
        log.info("更新任务，id={}", dto.getId());
        Task task = taskService.update(dto);
        return Result.success(task);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除任务，id={}", id);
        taskService.delete(id);
        return Result.success();
    }

    @GetMapping("/get/{id}")
    public Result<Task> get(@PathVariable Long id) {
        log.info("获取任务详情，id={}", id);
        Task task = taskService.getById(id);
        return Result.success(task);
    }

    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody TaskQueryDTO dto) {
        log.info("查询任务列表");
        List<Task> list = taskService.list(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Result.success(result);
    }
}
