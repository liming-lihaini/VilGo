package com.village.controller;

import com.village.dto.*;
import com.village.entity.PublicActivity;
import com.village.service.PublicActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/publicActivity")
@RequiredArgsConstructor
public class PublicActivityController {

    private final PublicActivityService publicActivityService;

    @PostMapping("/create")
    public Result<PublicActivity> create(@Valid @RequestBody PublicActivityDTO dto) {
        log.info("创建公益活动，name={}", dto.getName());
        PublicActivity activity = publicActivityService.create(dto);
        return Result.success(activity);
    }

    @PutMapping("/update")
    public Result<PublicActivity> update(@Valid @RequestBody PublicActivityDTO dto) {
        log.info("更新公益活动，id={}", dto.getId());
        PublicActivity activity = publicActivityService.update(dto);
        return Result.success(activity);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除公益活动，id={}", id);
        publicActivityService.delete(id);
        return Result.success();
    }

    @GetMapping("/get/{id}")
    public Result<PublicActivity> get(@PathVariable Long id) {
        log.info("获取公益活动详情，id={}", id);
        PublicActivity activity = publicActivityService.getById(id);
        return Result.success(activity);
    }

    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody PublicActivityQueryDTO dto) {
        log.info("查询公益活动列表");
        List<PublicActivity> list = publicActivityService.list(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Result.success(result);
    }

    @PostMapping("/statistics")
    public Result<ActivityStatisticsDTO> statistics(@RequestBody PublicActivityQueryDTO dto) {
        log.info("获取活动统计数据");
        ActivityStatisticsDTO stats = publicActivityService.statistics(dto);
        return Result.success(stats);
    }
}