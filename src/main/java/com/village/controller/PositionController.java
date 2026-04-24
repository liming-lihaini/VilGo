package com.village.controller;

import com.village.dto.*;
import com.village.dto.PositionDTO;
import com.village.entity.Position;
import com.village.service.PositionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/position")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    @PostMapping("/create")
    public Result<Position> create(@Valid @RequestBody PositionDTO dto) {
        log.info("新增职位，name={}", dto.getName());
        Position position = positionService.create(dto);
        return Result.success(position);
    }

    @PutMapping("/update")
    public Result<Position> update(@Valid @RequestBody PositionDTO dto) {
        log.info("更新职位，id={}", dto.getId());
        Position position = positionService.update(dto);
        return Result.success(position);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除职位，id={}", id);
        positionService.delete(id);
        return Result.success();
    }

    @GetMapping("/get/{id}")
    public Result<Position> get(@PathVariable Long id) {
        log.info("获取职位详情，id={}", id);
        Position position = positionService.getById(id);
        return Result.success(position);
    }

    @GetMapping("/list")
    public Result<List<Position>> list() {
        log.info("查询职位列表");
        List<Position> list = positionService.list();
        return Result.success(list);
    }
}
