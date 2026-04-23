package com.village.controller;

import com.village.dto.*;
import com.village.entity.Household;
import com.village.entity.HouseholdChange;
import com.village.entity.HouseholdIncome;
import com.village.entity.HouseholdMember;
import com.village.entity.Resident;
import com.village.service.HouseholdChangeService;
import com.village.service.HouseholdIncomeService;
import com.village.service.HouseholdMemberService;
import com.village.service.HouseholdService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 户籍档案控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/household")
@RequiredArgsConstructor
public class HouseholdController {

    private final HouseholdService householdService;
    private final HouseholdMemberService householdMemberService;
    private final HouseholdIncomeService householdIncomeService;
    private final HouseholdChangeService householdChangeService;

    /**
     * 新增家庭户
     */
    @PostMapping("/create")
    public Result<Household> create(@Valid @RequestBody HouseholdDTO dto) {
        log.info("新增家庭户，householdNo={}", dto.getHouseholdNo());
        Household household = householdService.create(dto);
        return Result.success(household);
    }

    /**
     * 更新家庭户
     */
    @PutMapping("/update")
    public Result<Household> update(@Valid @RequestBody HouseholdDTO dto) {
        log.info("更新家庭户，id={}", dto.getId());
        Household household = householdService.update(dto);
        return Result.success(household);
    }

    /**
     * 删除家庭户（软删除）
     */
    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除家庭户，id={}", id);
        householdService.delete(id);
        return Result.success();
    }

    /**
     * 获取家庭户详情（包含成员、收入、变动记录）
     */
    @GetMapping("/detail/{id}")
    public Result<HouseholdDetailDTO> detail(@PathVariable Long id) {
        log.info("获取家庭户详情，id={}", id);
        HouseholdDetailDTO detail = householdService.getDetail(id);
        return Result.success(detail);
    }

    /**
     * 查询家庭户列表
     */
    @PostMapping("/list")
    public Result<Map<String, Object>> list(@RequestBody HouseholdQueryDTO dto) {
        log.info("查询家庭户列表，pageNum={}, pageSize={}", dto.getPageNum(), dto.getPageSize());
        List<Household> list = householdService.list(dto);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", list.size());
        return Result.success(result);
    }

    /**
     * 从村民档案同步户主（创建家庭户）
     */
    @PostMapping("/sync/{residentId}")
    public Result<Household> syncFromResident(@PathVariable Long residentId) {
        log.info("从村民档案同步户主，residentId={}", residentId);
        Household household = householdService.syncFromResident(residentId);
        return Result.success(household);
    }

    /**
     * 同步所有户主
     */
    @PostMapping("/sync-all")
    public Result<Void> syncAllFromResidents() {
        log.info("同步所有户主");
        householdService.syncAllFromResidents();
        return Result.success();
    }

    // ========== 家庭成员管理 ==========

    /**
     * 添加家庭成员
     */
    @PostMapping("/member/add")
    public Result<HouseholdMember> addMember(@Valid @RequestBody HouseholdMemberDTO dto) {
        log.info("添加家庭成员，householdId={}, residentId={}", dto.getHouseholdId(), dto.getResidentId());
        HouseholdMember member = householdMemberService.addMember(dto);
        return Result.success(member);
    }

    /**
     * 移除家庭成员
     */
    @DeleteMapping("/member/remove/{id}")
    public Result<Void> removeMember(@PathVariable Long id) {
        log.info("移除家庭成员，id={}", id);
        householdMemberService.removeMember(id);
        return Result.success();
    }

    /**
     * 获取家庭成员列表
     */
    @GetMapping("/members/{householdId}")
    public Result<List<HouseholdMemberDetailDTO>> getMembers(@PathVariable Long householdId) {
        log.info("获取家庭成员列表，householdId={}", householdId);
        List<HouseholdMemberDetailDTO> members = householdMemberService.getMembersByHouseholdId(householdId);
        return Result.success(members);
    }

    // ========== 年度收入管理 ==========

    /**
     * 新增/更新年度收入
     */
    @PostMapping("/income/save")
    public Result<HouseholdIncome> saveIncome(@Valid @RequestBody HouseholdIncomeDTO dto) {
        log.info("保存年度收入，householdId={}, year={}", dto.getHouseholdId(), dto.getYear());
        HouseholdIncome income = householdIncomeService.saveIncome(dto);
        return Result.success(income);
    }

    /**
     * 删除年度收入
     */
    @DeleteMapping("/income/delete/{id}")
    public Result<Void> deleteIncome(@PathVariable Long id) {
        log.info("删除年度收入，id={}", id);
        householdIncomeService.deleteIncome(id);
        return Result.success();
    }

    /**
     * 查询家庭年度收入列表
     */
    @GetMapping("/incomes/{householdId}")
    public Result<List<HouseholdIncome>> getIncomes(@PathVariable Long householdId) {
        log.info("查询年度收入列表，householdId={}", householdId);
        List<HouseholdIncome> incomes = householdIncomeService.listByHouseholdId(householdId);
        return Result.success(incomes);
    }

    // ========== 户籍变动管理 ==========

    /**
     * 登记户籍变动
     */
    @PostMapping("/change/create")
    public Result<HouseholdChange> createChange(@Valid @RequestBody HouseholdChangeDTO dto) {
        log.info("登记户籍变动，householdId={}, type={}", dto.getHouseholdId(), dto.getChangeType());
        HouseholdChange change = householdChangeService.createChange(dto);
        return Result.success(change);
    }

    /**
     * 删除户籍变动记录
     */
    @DeleteMapping("/change/delete/{id}")
    public Result<Void> deleteChange(@PathVariable Long id) {
        log.info("删除户籍变动记录，id={}", id);
        householdChangeService.deleteChange(id);
        return Result.success();
    }

    /**
     * 查询家庭户籍变动记录列表
     */
    @GetMapping("/changes/{householdId}")
    public Result<List<HouseholdChange>> getChanges(@PathVariable Long householdId) {
        log.info("查询户籍变动列表，householdId={}", householdId);
        List<HouseholdChange> changes = householdChangeService.listByHouseholdId(householdId);
        return Result.success(changes);
    }
}