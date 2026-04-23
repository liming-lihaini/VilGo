package com.village.controller;

import com.alibaba.excel.EasyExcel;
import com.village.dto.ResidentDTO;
import com.village.dto.ResidentImportResultDTO;
import com.village.dto.ResidentQueryDTO;
import com.village.dto.ResidentStatisticsDTO;
import com.village.dto.Result;
import com.village.entity.Resident;
import com.village.service.ResidentService;
import com.village.util.ExcelUtil;
import com.village.util.ResidentImportListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 村民档案控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/resident")
@RequiredArgsConstructor
public class ResidentController {

    private final ResidentService residentService;

    /**
     * 新增村民档案
     */
    @PostMapping("/create")
    public Result<Resident> create(@Valid @RequestBody ResidentDTO dto) {
        log.info("新增村民档案，name={}", dto.getName());
        Resident resident = residentService.create(dto);
        return Result.success(resident);
    }

    /**
     * 更新村民档案
     */
    @PutMapping("/update")
    public Result<Resident> update(@Valid @RequestBody ResidentDTO dto) {
        log.info("更新村民档案，id={}", dto.getId());
        Resident resident = residentService.update(dto);
        return Result.success(resident);
    }

    /**
     * 删除村民档案（软删除）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除村民档案，id={}", id);
        residentService.delete(id);
        return Result.success();
    }

    /**
     * 获取村民档案
     */
    @GetMapping("/get/{id}")
    public Result<Resident> get(@PathVariable Long id) {
        log.info("获取村民档案，id={}", id);
        Resident resident = residentService.getById(id);
        return Result.success(resident);
    }

    /**
     * 查询村民列表
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody ResidentQueryDTO dto) {
        log.info("查询村民列表，pageNum={}, pageSize={}", dto.getPageNum(), dto.getPageSize());
        List<Resident> list = residentService.list(dto);

        // 构建返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Result.success(result);
    }

    /**
     * 销户登记
     */
    @PutMapping("/cancel/{id}")
    public Result<Void> cancel(@PathVariable Long id) {
        log.info("销户登记，id={}", id);
        residentService.cancel(id);
        return Result.success();
    }

    /**
     * 人口统计
     */
    @PostMapping("/statistics")
    public Result<ResidentStatisticsDTO> statistics() {
        log.info("人口统计");
        ResidentStatisticsDTO dto = residentService.statistics();
        return Result.success(dto);
    }

    /**
     * Excel导出
     */
    @PostMapping("/export")
    public void export(@RequestBody ResidentQueryDTO dto, HttpServletResponse response) throws IOException {
        log.info("Excel导出");

        List<Resident> list = residentService.listForExport(dto);

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("村民档案", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        // 写入Excel
        EasyExcel.write(response.getOutputStream())
                .head(ExcelUtil.getResidentHead())
                .sheet()
                .doWrite(list);
    }

    /**
     * Excel导入
     */
    @PostMapping("/import")
    public Result<ResidentImportResultDTO> importExcel(MultipartFile file) throws IOException {
        log.info("Excel导入");

        ResidentImportResultDTO resultDTO = new ResidentImportResultDTO();

        try {
            EasyExcel.read(file.getInputStream())
                    .head(Resident.class)
                    .registerReadListener(new ResidentImportListener(residentService, resultDTO))
                    .sheet()
                    .doRead();
        } catch (Exception e) {
            log.error("Excel导入失败", e);
            return Result.error("导入失败: " + e.getMessage());
        }

        return Result.success(resultDTO);
    }

    /**
     * 下载导入模板
     */
    @GetMapping("/import/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        log.info("下载导入模板");

        // 设置响应头
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("村民档案导入模板", StandardCharsets.UTF_8);
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName + ".xlsx");

        // 创建模板数据（表头）
        EasyExcel.write(response.getOutputStream())
                .head(ExcelUtil.getResidentHead())
                .sheet()
                .doWrite(java.util.Collections.emptyList());
    }
}