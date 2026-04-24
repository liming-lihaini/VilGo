package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.village.dao.PublicActivityDao;
import com.village.dto.ActivityStatisticsDTO;
import com.village.dto.PublicActivityDTO;
import com.village.dto.PublicActivityQueryDTO;
import com.village.entity.PublicActivity;
import com.village.exception.BusinessException;
import com.village.service.PublicActivityService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PublicActivityServiceImpl implements PublicActivityService {

    private final PublicActivityDao activityDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PublicActivity create(PublicActivityDTO dto) {
        if (dto.getName() == null || dto.getName().trim().isEmpty()) {
            throw new BusinessException("活动名称不能为空");
        }

        // 检测活动时间段冲突
        checkTimeConflict(null, dto.getStartTime(), dto.getEndTime());

        PublicActivity activity = new PublicActivity();
        activity.setName(dto.getName());
        activity.setActivityType(dto.getActivityType());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setLocation(dto.getLocation());
        activity.setContent(dto.getContent());
        activity.setRequirement(dto.getRequirement());
        activity.setStatus("pending");
        activity.setCreator(dto.getCreator());
        activity.setCreateTime(DateUtils.now());
        activity.setUpdateTime(DateUtils.now());
        activity.setDeleted(0);

        activityDao.insert(activity);
        log.info("新增公益活动成功，id={}, name={}", activity.getId(), activity.getName());
        return activity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PublicActivity update(PublicActivityDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("活动ID不能为空");
        }

        PublicActivity activity = activityDao.selectById(dto.getId());
        if (activity == null || activity.getDeleted() == 1) {
            throw new BusinessException("活动不存在");
        }

        // 已归档的活动不能修改
        if ("archived".equals(activity.getStatus())) {
            throw new BusinessException("已归档的活动不能修改");
        }

        // 检测活动时间冲突
        checkTimeConflict(dto.getId(), dto.getStartTime(), dto.getEndTime());

        activity.setName(dto.getName());
        activity.setActivityType(dto.getActivityType());
        activity.setStartTime(dto.getStartTime());
        activity.setEndTime(dto.getEndTime());
        activity.setLocation(dto.getLocation());
        activity.setContent(dto.getContent());
        activity.setRequirement(dto.getRequirement());

        if (StringUtils.hasText(dto.getStatus())) {
            // 状态流转校验
            if (!isValidStatusTransition(activity.getStatus(), dto.getStatus())) {
                throw new BusinessException("活动状态流转不合理");
            }
            activity.setStatus(dto.getStatus());
        }

        if (StringUtils.hasText(dto.getSummary())) {
            activity.setSummary(dto.getSummary());
        }

        activity.setUpdateTime(DateUtils.now());
        activityDao.updateById(activity);
        log.info("更新公益活动成功，id={}", dto.getId());
        return activity;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        PublicActivity activity = activityDao.selectById(id);
        if (activity == null || activity.getDeleted() == 1) {
            throw new BusinessException("活动不存在");
        }

        // 进行中或已归档的活动不能删除
        if ("ongoing".equals(activity.getStatus()) || "archived".equals(activity.getStatus())) {
            throw new BusinessException("进行中或已归档的活动不能删除");
        }

        activity.setDeleted(1);
        activity.setUpdateTime(DateUtils.now());
        activityDao.updateById(activity);
        log.info("删除公益活动成功，id={}", id);
    }

    @Override
    public PublicActivity getById(Long id) {
        return activityDao.selectById(id);
    }

    @Override
    public List<PublicActivity> list(PublicActivityQueryDTO dto) {
        LambdaQueryWrapper<PublicActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PublicActivity::getDeleted, 0);

        if (StringUtils.hasText(dto.getName())) {
            wrapper.like(PublicActivity::getName, dto.getName());
        }
        if (StringUtils.hasText(dto.getActivityType())) {
            wrapper.eq(PublicActivity::getActivityType, dto.getActivityType());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            wrapper.eq(PublicActivity::getStatus, dto.getStatus());
        }
        if (StringUtils.hasText(dto.getStartTime())) {
            wrapper.ge(PublicActivity::getStartTime, dto.getStartTime());
        }
        if (StringUtils.hasText(dto.getEndTime())) {
            wrapper.le(PublicActivity::getEndTime, dto.getEndTime());
        }

        wrapper.orderByDesc(PublicActivity::getCreateTime);

        Page<PublicActivity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<PublicActivity> result = activityDao.selectPage(page, wrapper);
        return result.getRecords();
    }

    @Override
    public ActivityStatisticsDTO statistics(PublicActivityQueryDTO dto) {
        ActivityStatisticsDTO stats = new ActivityStatisticsDTO();

        LambdaQueryWrapper<PublicActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PublicActivity::getDeleted, 0);

        if (StringUtils.hasText(dto.getActivityType())) {
            wrapper.eq(PublicActivity::getActivityType, dto.getActivityType());
            stats.setActivityType(dto.getActivityType());
        }
        if (StringUtils.hasText(dto.getStartTime())) {
            wrapper.ge(PublicActivity::getStartTime, dto.getStartTime());
        }
        if (StringUtils.hasText(dto.getEndTime())) {
            wrapper.le(PublicActivity::getEndTime, dto.getEndTime());
        }

        List<PublicActivity> activities = activityDao.selectList(wrapper);
        stats.setTotalActivities(activities.size());

        return stats;
    }

    /**
     * 检测活动时间冲突
     */
    private void checkTimeConflict(Long excludeId, String startTime, String endTime) {
        if (startTime == null || endTime == null) {
            return;
        }

        LambdaQueryWrapper<PublicActivity> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(PublicActivity::getDeleted, 0);
        wrapper.and(w -> w
                .le(PublicActivity::getStartTime, endTime)
                .ge(PublicActivity::getStartTime, startTime)
        );

        if (excludeId != null) {
            wrapper.ne(PublicActivity::getId, excludeId);
        }

        Long count = activityDao.selectCount(wrapper);
        if (count > 0) {
            throw new BusinessException("活动时间与现有活动冲突");
        }
    }

    /**
     * 验证活动状态流转
     */
    private boolean isValidStatusTransition(String oldStatus, String newStatus) {
        if ("pending".equals(oldStatus)) {
            return "pending".equals(newStatus) || "ongoing".equals(newStatus);
        }
        if ("ongoing".equals(oldStatus)) {
            return "ongoing".equals(newStatus) || "archived".equals(newStatus);
        }
        if ("archived".equals(oldStatus)) {
            return "archived".equals(newStatus);
        }
        return true;
    }
}