package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.village.dao.PartyActivityDao;
import com.village.dao.PartyDuesDao;
import com.village.dao.PartyMemberDao;
import com.village.dao.ResidentDao;
import com.village.dto.PartyActivityDTO;
import com.village.dto.PartyDuesDTO;
import com.village.dto.PartyDuesQueryDTO;
import com.village.dto.PartyMemberDTO;
import com.village.dto.PartyMemberOutputDTO;
import com.village.dto.PartyMemberQueryDTO;
import com.village.entity.PartyActivity;
import com.village.entity.PartyDues;
import com.village.entity.PartyMember;
import com.village.entity.Resident;
import com.village.enums.PartyMemberStatus;
import com.village.exception.BusinessException;
import com.village.service.PartyWorkService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 党务管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PartyWorkServiceImpl implements PartyWorkService {

    private final PartyMemberDao partyMemberDao;
    private final PartyActivityDao partyActivityDao;
    private final PartyDuesDao partyDuesDao;
    private final ResidentDao residentDao;

    // ==================== 党员档案相关 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PartyMember create(PartyMemberDTO dto) {
        // 校验身份证号唯一性
        PartyMember existing = partyMemberDao.selectByIdCardIgnoreDeleted(dto.getIdCard());
        if (existing != null) {
            throw new BusinessException("该身份证号已存在党员档案");
        }

        // 校验身份证号在 residents 表中存在（应用层校验）
        Resident resident = validateIdCardExists(dto.getIdCard());

        // 校验党员状态相关时间
        validateStatusAndDates(dto.getStatus(), dto.getJoinDate(), dto.getConvertDate());

        // 转换为实体
        PartyMember member = toEntity(dto);
        member.setCreateTime(DateUtils.now());
        member.setUpdateTime(DateUtils.now());
        member.setDeleted(0);

        // 如果未指定状态，默认为积极分子
        if (!StringUtils.hasText(member.getStatus())) {
            member.setStatus(PartyMemberStatus.ACTIVIST.getCode());
        }

        partyMemberDao.insert(member);
        log.info("新增党员档案成功，id={}, idCard={}", member.getId(), member.getIdCard());
        return member;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PartyMember update(PartyMemberDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("ID不能为空");
        }

        PartyMember existing = partyMemberDao.selectById(dto.getId());
        if (existing == null) {
            throw new BusinessException("记录不存在");
        }

        // 获取当前状态用于校验状态流转
        PartyMemberStatus currentStatus = PartyMemberStatus.fromCode(existing.getStatus());
        PartyMemberStatus newStatus = PartyMemberStatus.fromCode(dto.getStatus());

        // 如果状态变更，校验状态流转
        if (newStatus != null && !newStatus.equals(currentStatus)) {
            if (!PartyMemberStatus.canTransition(currentStatus, newStatus)) {
                throw new BusinessException("党员状态流转顺序不正确，必须按积极分子 → 发展对象 → 预备党员 → 正式党员 的顺序");
            }
        }

        // 校验入党时间和转正时间
        validateStatusAndDates(dto.getStatus(), dto.getJoinDate(), dto.getConvertDate());

        // 更新字段
        existing.setName(dto.getName());
        existing.setGender(dto.getGender());
        existing.setBirthDate(dto.getBirthDate());
        existing.setPhone(dto.getPhone());
        existing.setAddress(dto.getAddress());
        existing.setJoinDate(dto.getJoinDate());
        existing.setConvertDate(dto.getConvertDate());
        if (StringUtils.hasText(dto.getStatus())) {
            existing.setStatus(dto.getStatus());
        }

        partyMemberDao.updateById(existing);
        log.info("更新党员档案成功，id={}", existing.getId());
        return existing;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        PartyMember member = partyMemberDao.selectById(id);
        if (member == null) {
            throw new BusinessException("记录不存在");
        }

        // 使用 wrapper 方式更新，避免被 @TableLogic 拦截
        LambdaQueryWrapper<PartyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartyMember::getId, id);
        PartyMember update = new PartyMember();
        update.setDeleted(1);
        partyMemberDao.update(update, wrapper);
        log.info("删除党员档案成功，id={}", id);
    }

    @Override
    public PartyMember getById(Long id) {
        PartyMember member = partyMemberDao.selectById(id);
        if (member == null) {
            throw new BusinessException("记录不存在");
        }
        return member;
    }

    @Override
    public List<PartyMemberOutputDTO> list(PartyMemberQueryDTO dto) {
        LambdaQueryWrapper<PartyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartyMember::getDeleted, 0);

        if (StringUtils.hasText(dto.getName())) {
            wrapper.like(PartyMember::getName, dto.getName());
        }
        if (StringUtils.hasText(dto.getIdCard())) {
            wrapper.like(PartyMember::getIdCard, dto.getIdCard());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            wrapper.eq(PartyMember::getStatus, dto.getStatus());
        }
        if (StringUtils.hasText(dto.getGender())) {
            wrapper.eq(PartyMember::getGender, dto.getGender());
        }

        // 排序
        wrapper.orderByDesc(PartyMember::getCreateTime);

        // 分页
        Page<PartyMember> page = new Page<>(dto.getPageNum() != null ? dto.getPageNum() : 1,
                dto.getPageSize() != null ? dto.getPageSize() : 10);
        IPage<PartyMember> result = partyMemberDao.selectPage(page, wrapper);

        // 转换为输出 DTO
        List<PartyMemberOutputDTO> outputList = new ArrayList<>();
        for (PartyMember member : result.getRecords()) {
            PartyMemberOutputDTO output = toOutputDTO(member);
            // 尝试从 residents 表获取姓名
            Resident resident = residentDao.selectByIdCard(member.getIdCard());
            if (resident != null) {
                output.setName(resident.getName());
            }
            outputList.add(output);
        }
        return outputList;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PartyMember updateStatus(Long id, String newStatus) {
        PartyMember member = partyMemberDao.selectById(id);
        if (member == null) {
            throw new BusinessException("记录不存在");
        }

        PartyMemberStatus currentStatus = PartyMemberStatus.fromCode(member.getStatus());
        PartyMemberStatus targetStatus = PartyMemberStatus.fromCode(newStatus);

        if (targetStatus == null) {
            throw new BusinessException("无效的党员状态");
        }

        // 校验状态流转
        if (!PartyMemberStatus.canTransition(currentStatus, targetStatus)) {
            throw new BusinessException("党员状态流转顺序不正确，必须按积极分子 → 发展对象 → 预备党员 → 正式党员 的顺序");
        }

        member.setStatus(newStatus);
        partyMemberDao.updateById(member);
        log.info("更新党员状态成功，id={}, status={}", id, newStatus);
        return member;
    }

    // ==================== 党务活动相关 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PartyActivity createActivity(PartyActivityDTO dto) {
        // 校验党员是否存在
        PartyMember member = partyMemberDao.selectById(dto.getMemberId());
        if (member == null) {
            throw new BusinessException("党员不存在");
        }

        PartyActivity activity = toActivityEntity(dto);
        activity.setCreateTime(DateUtils.now());
        activity.setUpdateTime(DateUtils.now());
        activity.setDeleted(0);

        partyActivityDao.insert(activity);
        log.info("新增党务活动成功，id={}, theme={}", activity.getId(), activity.getTheme());
        return activity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PartyActivity updateActivity(PartyActivityDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("ID不能为空");
        }

        PartyActivity activity = partyActivityDao.selectById(dto.getId());
        if (activity == null) {
            throw new BusinessException("记录不存在");
        }

        activity.setTheme(dto.getTheme());
        activity.setActivityType(dto.getActivityType());
        activity.setActivityTime(dto.getActivityTime());
        activity.setLocation(dto.getLocation());
        activity.setParticipation(dto.getParticipation());
        activity.setContent(dto.getContent());

        partyActivityDao.updateById(activity);
        log.info("更新党务活动成功，id={}", activity.getId());
        return activity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteActivity(Long id) {
        PartyActivity activity = partyActivityDao.selectById(id);
        if (activity == null) {
            throw new BusinessException("记录不存在");
        }

        LambdaQueryWrapper<PartyActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartyActivity::getId, id);
        PartyActivity update = new PartyActivity();
        update.setDeleted(1);
        partyActivityDao.update(update, wrapper);
        log.info("删除党务活动成功，id={}", id);
    }

    @Override
    public PartyActivity getActivityById(Long id) {
        PartyActivity activity = partyActivityDao.selectById(id);
        if (activity == null) {
            throw new BusinessException("记录不存在");
        }
        return activity;
    }

    @Override
    public List<PartyActivity> listActivities(PartyActivityDTO dto) {
        LambdaQueryWrapper<PartyActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartyActivity::getDeleted, 0);

        if (dto.getMemberId() != null) {
            wrapper.eq(PartyActivity::getMemberId, dto.getMemberId());
        }
        if (StringUtils.hasText(dto.getTheme())) {
            wrapper.like(PartyActivity::getTheme, dto.getTheme());
        }
        if (StringUtils.hasText(dto.getActivityType())) {
            wrapper.eq(PartyActivity::getActivityType, dto.getActivityType());
        }

        wrapper.orderByDesc(PartyActivity::getActivityTime);
        return partyActivityDao.selectList(wrapper);
    }

    // ==================== 党费收缴相关 ====================

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PartyDues createDues(PartyDuesDTO dto) {
        // 校验党员是否存在
        PartyMember member = partyMemberDao.selectById(dto.getMemberId());
        if (member == null) {
            throw new BusinessException("党员不存在");
        }

        PartyDues dues = toDuesEntity(dto);
        dues.setCreateTime(DateUtils.now());
        dues.setDeleted(0);

        // 默认状态为未缴
        if (!StringUtils.hasText(dues.getStatus())) {
            dues.setStatus("unpaid");
        }

        partyDuesDao.insert(dues);
        log.info("新增党费记录成功，id={}, memberId={}, amount={}", dues.getId(), dues.getMemberId(), dues.getAmount());
        return dues;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PartyDues updateDues(PartyDuesDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("ID不能为空");
        }

        PartyDues dues = partyDuesDao.selectById(dto.getId());
        if (dues == null) {
            throw new BusinessException("记录不存在");
        }

        dues.setAmount(dto.getAmount());
        dues.setPayMonth(dto.getPayMonth());
        dues.setPayDate(dto.getPayDate());
        dues.setStatus(dto.getStatus());
        dues.setRemark(dto.getRemark());

        partyDuesDao.updateById(dues);
        log.info("更新党费记录成功，id={}", dues.getId());
        return dues;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteDues(Long id) {
        PartyDues dues = partyDuesDao.selectById(id);
        if (dues == null) {
            throw new BusinessException("记录不存在");
        }

        LambdaQueryWrapper<PartyDues> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartyDues::getId, id);
        PartyDues update = new PartyDues();
        update.setDeleted(1);
        partyDuesDao.update(update, wrapper);
        log.info("删除党费记录成功，id={}", id);
    }

    @Override
    public PartyDues getDuesById(Long id) {
        PartyDues dues = partyDuesDao.selectById(id);
        if (dues == null) {
            throw new BusinessException("记录不存在");
        }
        return dues;
    }

    @Override
    public List<PartyDues> listDues(PartyDuesQueryDTO dto) {
        LambdaQueryWrapper<PartyDues> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartyDues::getDeleted, 0);

        if (dto.getMemberId() != null) {
            wrapper.eq(PartyDues::getMemberId, dto.getMemberId());
        }
        if (StringUtils.hasText(dto.getPayMonth())) {
            wrapper.eq(PartyDues::getPayMonth, dto.getPayMonth());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            wrapper.eq(PartyDues::getStatus, dto.getStatus());
        }

        wrapper.orderByDesc(PartyDues::getPayMonth);

        Page<PartyDues> page = new Page<>(dto.getPageNum() != null ? dto.getPageNum() : 1,
                dto.getPageSize() != null ? dto.getPageSize() : 10);
        IPage<PartyDues> result = partyDuesDao.selectPage(page, wrapper);
        return result.getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PartyDues markAsPaid(Long id) {
        PartyDues dues = partyDuesDao.selectById(id);
        if (dues == null) {
            throw new BusinessException("记录不存在");
        }

        dues.setStatus("paid");
        dues.setPayDate(DateUtils.now());
        partyDuesDao.updateById(dues);
        log.info("标记党费为已缴成功，id={}", id);
        return dues;
    }

    // ==================== 统计相关 ====================

    @Override
    public Object partyMemberStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 查询所有未删除的党员
        LambdaQueryWrapper<PartyMember> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartyMember::getDeleted, 0);
        List<PartyMember> members = partyMemberDao.selectList(wrapper);

        result.put("totalCount", members.size());

        // 按状态统计
        Map<String, Long> statusCount = members.stream()
                .filter(m -> StringUtils.hasText(m.getStatus()))
                .collect(Collectors.groupingBy(PartyMember::getStatus, Collectors.counting()));
        result.put("statusCount", statusCount);

        return result;
    }

    @Override
    public Object partyDuesStatistics() {
        Map<String, Object> result = new HashMap<>();

        // 查询所有未删除的党费记录
        LambdaQueryWrapper<PartyDues> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PartyDues::getDeleted, 0);
        List<PartyDues> duesList = partyDuesDao.selectList(wrapper);

        // 已缴统计
        BigDecimal paidTotal = duesList.stream()
                .filter(d -> "paid".equals(d.getStatus()))
                .map(PartyDues::getAmount)
                .filter(Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        result.put("paidTotal", paidTotal);

        // 未缴统计
        long unpaidCount = duesList.stream()
                .filter(d -> "unpaid".equals(d.getStatus()))
                .count();
        result.put("unpaidCount", unpaidCount);

        // 按月份统计
        Map<String, BigDecimal> monthlyCount = duesList.stream()
                .filter(d -> StringUtils.hasText(d.getPayMonth()))
                .collect(Collectors.groupingBy(
                        PartyDues::getPayMonth,
                       Collectors.mapping(PartyDues::getAmount, Collectors.reducing(BigDecimal.ZERO, BigDecimal::add))
                ));
        result.put("monthlyCount", monthlyCount);

        return result;
    }

    // ==================== 私有方法 ====================

    /**
     * 校验身份证号在 residents 表中存在
     */
    private Resident validateIdCardExists(String idCard) {
        Resident resident = residentDao.selectByIdCard(idCard);
        if (resident == null) {
            throw new BusinessException("身份证号不存在于人事档案中，请先添加村民信息");
        }
        return resident;
    }

    /**
     * 校验党员状态相关时间
     */
    private void validateStatusAndDates(String status, String joinDate, String convertDate) {
        if (!StringUtils.hasText(status)) {
            return;
        }

        PartyMemberStatus memberStatus = PartyMemberStatus.fromCode(status);
        if (memberStatus == null) {
            return;
        }

        // 入党时间 <= 转正时间校验
        if (StringUtils.hasText(joinDate) && StringUtils.hasText(convertDate)) {
            if (joinDate.compareTo(convertDate) > 0) {
                throw new BusinessException("入党时间不能晚于转正时间");
            }
        }
    }

    /**
     * DTO 转换为实体
     */
    private PartyMember toEntity(PartyMemberDTO dto) {
        PartyMember member = new PartyMember();
        member.setIdCard(dto.getIdCard());
        member.setName(dto.getName());
        member.setGender(dto.getGender());
        member.setBirthDate(dto.getBirthDate());
        member.setPhone(dto.getPhone());
        member.setAddress(dto.getAddress());
        member.setJoinDate(dto.getJoinDate());
        member.setConvertDate(dto.getConvertDate());
        member.setStatus(dto.getStatus());
        member.setCreator(dto.getCreator());
        return member;
    }

    /**
     * 实体转换为输出 DTO
     */
    private PartyMemberOutputDTO toOutputDTO(PartyMember member) {
        PartyMemberOutputDTO dto = new PartyMemberOutputDTO();
        dto.setId(member.getId());
        dto.setIdCard(member.getIdCard());
        dto.setName(member.getName());
        dto.setGender(member.getGender());
        dto.setBirthDate(member.getBirthDate());
        dto.setPhone(member.getPhone());
        dto.setAddress(member.getAddress());
        dto.setJoinDate(member.getJoinDate());
        dto.setConvertDate(member.getConvertDate());
        dto.setStatus(member.getStatus());
        dto.setCreator(member.getCreator());
        dto.setCreateTime(member.getCreateTime());
        dto.setUpdateTime(member.getUpdateTime());
        return dto;
    }

    /**
     * DTO 转换为党务活动实体
     */
    private PartyActivity toActivityEntity(PartyActivityDTO dto) {
        PartyActivity activity = new PartyActivity();
        activity.setId(dto.getId());
        activity.setMemberId(dto.getMemberId());
        activity.setTheme(dto.getTheme());
        activity.setActivityType(dto.getActivityType());
        activity.setActivityTime(dto.getActivityTime());
        activity.setLocation(dto.getLocation());
        activity.setParticipation(dto.getParticipation());
        activity.setContent(dto.getContent());
        activity.setCreator(dto.getCreator());
        return activity;
    }

    /**
     * DTO 转换为党费实体
     */
    private PartyDues toDuesEntity(PartyDuesDTO dto) {
        PartyDues dues = new PartyDues();
        dues.setId(dto.getId());
        dues.setMemberId(dto.getMemberId());
        dues.setAmount(dto.getAmount());
        dues.setPayMonth(dto.getPayMonth());
        dues.setPayDate(dto.getPayDate());
        dues.setStatus(dto.getStatus());
        dues.setRemark(dto.getRemark());
        dues.setCreator(dto.getCreator());
        return dues;
    }
}