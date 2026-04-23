package com.village.controller;

import com.village.dto.*;
import com.village.entity.SpecialGroup;
import com.village.entity.HelpRecord;
import com.village.service.SpecialGroupService;
import com.village.service.HelpRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/specialGroup")
@RequiredArgsConstructor
public class SpecialGroupController {

    private final SpecialGroupService specialGroupService;
    private final HelpRecordService helpRecordService;

    @PostMapping("/create")
    public Result<SpecialGroup> create(@Valid @RequestBody SpecialGroupDTO dto) {
        log.info("新增特殊人群，idCard={}", dto.getIdCard());
        SpecialGroup group = specialGroupService.create(dto);
        return Result.success(group);
    }

    @PutMapping("/update")
    public Result<SpecialGroup> update(@Valid @RequestBody SpecialGroupDTO dto) {
        log.info("更新特殊人群，id={}", dto.getId());
        SpecialGroup group = specialGroupService.update(dto);
        return Result.success(group);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除特殊人群，id={}", id);
        specialGroupService.delete(id);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result<SpecialGroupDetailDTO> detail(@PathVariable Long id) {
        log.info("获取特殊人群详情，id={}", id);
        SpecialGroupDetailDTO detail = specialGroupService.getDetail(id);
        return Result.success(detail);
    }

    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody SpecialGroupQueryDTO dto) {
        log.info("查询特殊人群列表");
        List<SpecialGroup> list = specialGroupService.list(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Result.success(result);
    }

    @PostMapping("/statistics")
    public Result<Map<String, Long>> statistics() {
        log.info("特殊人群统计");
        Map<String, Long> stats = specialGroupService.statistics();
        return Result.success(stats);
    }

    @GetMapping("/remind")
    public Result<List<SpecialGroup>> remind() {
        log.info("获取到期提醒");
        List<SpecialGroup> list = specialGroupService.getRemindList();
        return Result.success(list);
    }

    @PostMapping("/export")
    public void export(@RequestBody SpecialGroupQueryDTO dto, HttpServletResponse response) throws IOException {
        log.info("特殊人群导出");

        List<SpecialGroup> list = specialGroupService.listForExport(dto);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("特殊人群", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        com.alibaba.excel.EasyExcel.write(response.getOutputStream())
                .head(com.village.util.ExcelUtil.getSpecialGroupHead())
                .sheet()
                .doWrite(list);
    }

    @PostMapping("/help/add")
    public Result<HelpRecord> addHelpRecord(@Valid @RequestBody HelpRecordDTO dto) {
        log.info("添加帮扶记录，specialGroupId={}", dto.getSpecialGroupId());
        HelpRecord record = helpRecordService.create(dto);
        return Result.success(record);
    }

    @DeleteMapping("/help/delete/{id}")
    public Result<Void> deleteHelpRecord(@PathVariable Long id) {
        log.info("删除帮扶记录，id={}", id);
        helpRecordService.delete(id);
        return Result.success();
    }

    @GetMapping("/help/list/{specialGroupId}")
    public Result<List<HelpRecord>> listHelpRecords(@PathVariable Long specialGroupId) {
        log.info("查询帮扶记录列表，specialGroupId={}", specialGroupId);
        List<HelpRecord> list = helpRecordService.listBySpecialGroupId(specialGroupId);
        return Result.success(list);
    }
}