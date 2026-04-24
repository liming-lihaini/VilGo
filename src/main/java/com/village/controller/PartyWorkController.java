package com.village.controller;

import com.village.dto.*;
import com.village.entity.PartyActivity;
import com.village.entity.PartyDues;
import com.village.entity.PartyMember;
import com.village.service.PartyWorkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 党务管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/partyWork")
@RequiredArgsConstructor
public class PartyWorkController {

    private final PartyWorkService partyWorkService;

    // ==================== 党员档案相关 ====================

    /**
     * 新增党员档案
     */
    @PostMapping("/member/create")
    public Result<PartyMember> createMember(@Valid @RequestBody PartyMemberDTO dto) {
        log.info("新增党员档案，idCard={}", dto.getIdCard());
        PartyMember member = partyWorkService.create(dto);
        return Result.success(member);
    }

    /**
     * 更新党员档案
     */
    @PutMapping("/member/update")
    public Result<PartyMember> updateMember(@Valid @RequestBody PartyMemberDTO dto) {
        log.info("更新党员档案，id={}", dto.getId());
        PartyMember member = partyWorkService.update(dto);
        return Result.success(member);
    }

    /**
     * 删除党员档案（软删除）
     */
    @DeleteMapping("/member/delete/{id}")
    public Result<Void> deleteMember(@PathVariable Long id) {
        log.info("删除党员档案，id={}", id);
        partyWorkService.delete(id);
        return Result.success();
    }

    /**
     * 获取党员档案
     */
    @GetMapping("/member/get/{id}")
    public Result<PartyMember> getMember(@PathVariable Long id) {
        log.info("获取党员档案，id={}", id);
        PartyMember member = partyWorkService.getById(id);
        return Result.success(member);
    }

    /**
     * 查询党员列表
     */
    @PostMapping("/member/list")
    public Result<Map<String, Object>> listMembers(@RequestBody PartyMemberQueryDTO dto) {
        log.info("查询党员列表，pageNum={}, pageSize={}", dto.getPageNum(), dto.getPageSize());
        List<PartyMemberOutputDTO> list = partyWorkService.list(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Result.success(result);
    }

    /**
     * 更新党员状态（状态流转）
     */
    @PutMapping("/member/updateStatus")
    public Result<PartyMember> updateMemberStatus(@RequestParam Long id, @RequestParam String status) {
        log.info("更新党员状态，id={}, status={}", id, status);
        PartyMember member = partyWorkService.updateStatus(id, status);
        return Result.success(member);
    }

    // ==================== 党务活动相关 ====================

    /**
     * 新增党务活动
     */
    @PostMapping("/activity/create")
    public Result<PartyActivity> createActivity(@Valid @RequestBody PartyActivityDTO dto) {
        log.info("新增党务活动，theme={}", dto.getTheme());
        PartyActivity activity = partyWorkService.createActivity(dto);
        return Result.success(activity);
    }

    /**
     * 更新党务活动
     */
    @PutMapping("/activity/update")
    public Result<PartyActivity> updateActivity(@Valid @RequestBody PartyActivityDTO dto) {
        log.info("更新党务活动，id={}", dto.getId());
        PartyActivity activity = partyWorkService.updateActivity(dto);
        return Result.success(activity);
    }

    /**
     * 删除党务活动（软删除）
     */
    @DeleteMapping("/activity/delete/{id}")
    public Result<Void> deleteActivity(@PathVariable Long id) {
        log.info("删除党务活动，id={}", id);
        partyWorkService.deleteActivity(id);
        return Result.success();
    }

    /**
     * 获取党务活动
     */
    @GetMapping("/activity/get/{id}")
    public Result<PartyActivity> getActivity(@PathVariable Long id) {
        log.info("获取党务活动，id={}", id);
        PartyActivity activity = partyWorkService.getActivityById(id);
        return Result.success(activity);
    }

    /**
     * 查询党务活动列表
     */
    @PostMapping("/activity/list")
    public Result<List<PartyActivity>> listActivities(@RequestBody PartyActivityDTO dto) {
        log.info("查询党务活动列表");
        List<PartyActivity> list = partyWorkService.listActivities(dto);
        return Result.success(list);
    }

    // ==================== 党费收缴相关 ====================

    /**
     * 新增党费记录
     */
    @PostMapping("/dues/create")
    public Result<PartyDues> createDues(@Valid @RequestBody PartyDuesDTO dto) {
        log.info("新增党费记录，memberId={}, amount={}", dto.getMemberId(), dto.getAmount());
        PartyDues dues = partyWorkService.createDues(dto);
        return Result.success(dues);
    }

    /**
     * 更新党费记录
     */
    @PutMapping("/dues/update")
    public Result<PartyDues> updateDues(@Valid @RequestBody PartyDuesDTO dto) {
        log.info("更新党费记录，id={}", dto.getId());
        PartyDues dues = partyWorkService.updateDues(dto);
        return Result.success(dues);
    }

    /**
     * 删除党费记录（软删除）
     */
    @DeleteMapping("/dues/delete/{id}")
    public Result<Void> deleteDues(@PathVariable Long id) {
        log.info("删除党费记录，id={}", id);
        partyWorkService.deleteDues(id);
        return Result.success();
    }

    /**
     * 获取党费记录
     */
    @GetMapping("/dues/get/{id}")
    public Result<PartyDues> getDues(@PathVariable Long id) {
        log.info("获取党费记录，id={}", id);
        PartyDues dues = partyWorkService.getDuesById(id);
        return Result.success(dues);
    }

    /**
     * 查询党费列表
     */
    @PostMapping("/dues/list")
    public Result<Map<String, Object>> listDues(@RequestBody PartyDuesQueryDTO dto) {
        log.info("查询党费列表，pageNum={}, pageSize={}", dto.getPageNum(), dto.getPageSize());
        List<PartyDues> list = partyWorkService.listDues(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Result.success(result);
    }

    /**
     * 标记党费为已缴
     */
    @PutMapping("/dues/markPaid/{id}")
    public Result<PartyDues> markDuesAsPaid(@PathVariable Long id) {
        log.info("标记党费为已缴，id={}", id);
        PartyDues dues = partyWorkService.markAsPaid(id);
        return Result.success(dues);
    }

    // ==================== 统计相关 ====================

    /**
     * 党员统计
     */
    @GetMapping("/statistics/member")
    public Result<Object> partyMemberStatistics() {
        log.info("党员统计");
        Object statistics = partyWorkService.partyMemberStatistics();
        return Result.success(statistics);
    }

    /**
     * 党费收缴统计
     */
    @GetMapping("/statistics/dues")
    public Result<Object> partyDuesStatistics() {
        log.info("党费收缴统计");
        Object statistics = partyWorkService.partyDuesStatistics();
        return Result.success(statistics);
    }
}