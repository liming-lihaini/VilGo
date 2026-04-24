package com.village.controller;

import com.village.dto.*;
import com.village.entity.TwoCommittee;
import com.village.service.TwoCommitteeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/twoCommittee")
@RequiredArgsConstructor
public class TwoCommitteeController {

    private final TwoCommitteeService twoCommitteeService;

    @PostMapping("/create")
    public Result<TwoCommittee> create(@Valid @RequestBody TwoCommitteeDTO dto) {
        log.info("新增两委成员，name={}", dto.getName());
        TwoCommittee member = twoCommitteeService.create(dto);
        return Result.success(member);
    }

    @PutMapping("/update")
    public Result<TwoCommittee> update(@Valid @RequestBody TwoCommitteeDTO dto) {
        log.info("更新两委成员，id={}", dto.getId());
        TwoCommittee member = twoCommitteeService.update(dto);
        return Result.success(member);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除两委成员，id={}", id);
        twoCommitteeService.delete(id);
        return Result.success();
    }

    @GetMapping("/get/{id}")
    public Result<TwoCommittee> get(@PathVariable Long id) {
        log.info("获取两委成员详情，id={}", id);
        TwoCommittee member = twoCommitteeService.getById(id);
        return Result.success(member);
    }

    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody TwoCommitteeQueryDTO dto) {
        log.info("查询两委成员列表");
        List<TwoCommittee> list = twoCommitteeService.list(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Result.success(result);
    }

    @PostMapping("/statistics")
    public Result<Map<String, Long>> statistics() {
        log.info("两委班子统计");
        Map<String, Long> stats = twoCommitteeService.statistics();
        return Result.success(stats);
    }
}
