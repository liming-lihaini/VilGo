package com.village.controller;

import com.village.dto.*;
import com.village.entity.ActivitySignup;
import com.village.service.ActivitySignupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/activitySignup")
@RequiredArgsConstructor
public class ActivitySignupController {

    private final ActivitySignupService signupService;

    @PostMapping("/create")
    public Result<ActivitySignup> create(@Valid @RequestBody ActivitySignupDTO dto) {
        log.info("创建活动报名，activityId={}, residentId={}", dto.getActivityId(), dto.getResidentId());
        ActivitySignup signup = signupService.create(dto);
        return Result.success(signup);
    }

    @PutMapping("/update")
    public Result<ActivitySignup> update(@Valid @RequestBody ActivitySignupDTO dto) {
        log.info("更新活动报名，id={}", dto.getId());
        ActivitySignup signup = signupService.update(dto);
        return Result.success(signup);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除活动报名，id={}", id);
        signupService.delete(id);
        return Result.success();
    }

    @GetMapping("/get/{id}")
    public Result<ActivitySignup> get(@PathVariable Long id) {
        log.info("获取活动报名详情，id={}", id);
        ActivitySignup signup = signupService.getById(id);
        return Result.success(signup);
    }

    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody ActivitySignupQueryDTO dto) {
        log.info("查询活动报名列表");
        List<ActivitySignup> list = signupService.list(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Result.success(result);
    }

    @PostMapping("/signIn/{id}")
    public Result<ActivitySignup> signIn(@PathVariable Long id) {
        log.info("活动签到，id={}", id);
        ActivitySignup signup = signupService.signIn(id);
        return Result.success(signup);
    }

    @PostMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        log.info("取消活动报名，id={}", id);
        signupService.cancelSignup(id);
        return Result.success();
    }
}