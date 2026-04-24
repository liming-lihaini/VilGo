package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.village.dao.ActivitySignupDao;
import com.village.dao.PublicActivityDao;
import com.village.dao.ResidentDao;
import com.village.dto.ActivitySignupDTO;
import com.village.dto.ActivitySignupQueryDTO;
import com.village.entity.ActivitySignup;
import com.village.entity.PublicActivity;
import com.village.entity.Resident;
import com.village.exception.BusinessException;
import com.village.service.ActivitySignupService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ActivitySignupServiceImpl implements ActivitySignupService {

    private final ActivitySignupDao signupDao;
    private final PublicActivityDao activityDao;
    private final ResidentDao residentDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivitySignup create(ActivitySignupDTO dto) {
        if (dto.getActivityId() == null) {
            throw new BusinessException("活动ID不能为空");
        }
        if (dto.getResidentId() == null) {
            throw new BusinessException("村民ID不能为空");
        }

        // 检查活动是否存在
        PublicActivity activity = activityDao.selectById(dto.getActivityId());
        if (activity == null || activity.getDeleted() == 1) {
            throw new BusinessException("活动不存在");
        }

        // 已归档的活动不能报名
        if ("archived".equals(activity.getStatus())) {
            throw new BusinessException("已归档的活动不能报名");
        }

        // 检查村民是否存在
        Resident resident = residentDao.selectById(dto.getResidentId());
        if (resident == null || resident.getDeleted() == 1) {
            throw new BusinessException("村民不存在");
        }

        // 检测报名时间冲突
        checkSignupConflict(dto.getActivityId(), dto.getResidentId());

        ActivitySignup signup = new ActivitySignup();
        signup.setActivityId(dto.getActivityId());
        signup.setResidentId(dto.getResidentId());
        signup.setStatus("registered");
        signup.setWorkHours(dto.getWorkHours());
        signup.setWorkValue(dto.getWorkValue());
        signup.setRemark(dto.getRemark());
        signup.setCreator(dto.getCreator());
        signup.setCreateTime(DateUtils.now());
        signup.setUpdateTime(DateUtils.now());
        signup.setDeleted(0);

        signupDao.insert(signup);
        log.info("新增活动报名成功，id={}, activityId={}, residentId={}",
                signup.getId(), signup.getActivityId(), signup.getResidentId());
        return signup;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivitySignup update(ActivitySignupDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("报名ID不能为空");
        }

        ActivitySignup signup = signupDao.selectById(dto.getId());
        if (signup == null || signup.getDeleted() == 1) {
            throw new BusinessException("报名记录不存在");
        }

        // 已取消的报名不能修改
        if ("cancelled".equals(signup.getStatus())) {
            throw new BusinessException("已取消的报名不能修改");
        }

        if (dto.getWorkHours() != null) {
            signup.setWorkHours(dto.getWorkHours());
        }
        if (dto.getWorkValue() != null) {
            signup.setWorkValue(dto.getWorkValue());
        }
        if (StringUtils.hasText(dto.getRemark())) {
            signup.setRemark(dto.getRemark());
        }

        signup.setUpdateTime(DateUtils.now());
        signupDao.updateById(signup);
        log.info("更新活动报名成功，id={}", dto.getId());
        return signup;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ActivitySignup signup = signupDao.selectById(id);
        if (signup == null || signup.getDeleted() == 1) {
            throw new BusinessException("报名记录不存在");
        }

        signup.setDeleted(1);
        signup.setUpdateTime(DateUtils.now());
        signupDao.updateById(signup);
        log.info("删除活动报名成功，id={}", id);
    }

    @Override
    public ActivitySignup getById(Long id) {
        return signupDao.selectById(id);
    }

    @Override
    public List<ActivitySignup> list(ActivitySignupQueryDTO dto) {
        LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ActivitySignup::getDeleted, 0);

        if (dto.getActivityId() != null) {
            wrapper.eq(ActivitySignup::getActivityId, dto.getActivityId());
        }
        if (dto.getResidentId() != null) {
            wrapper.eq(ActivitySignup::getResidentId, dto.getResidentId());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            wrapper.eq(ActivitySignup::getStatus, dto.getStatus());
        }

        wrapper.orderByDesc(ActivitySignup::getCreateTime);

        Page<ActivitySignup> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<ActivitySignup> result = signupDao.selectPage(page, wrapper);
        return result.getRecords();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ActivitySignup signIn(Long id) {
        ActivitySignup signup = signupDao.selectById(id);
        if (signup == null || signup.getDeleted() == 1) {
            throw new BusinessException("报名记录不存在");
        }

        if (!"registered".equals(signup.getStatus())) {
            throw new BusinessException("只有已报名的状态才能签到");
        }

        signup.setStatus("signed_in");
        signup.setSignInTime(DateUtils.now());
        signup.setUpdateTime(DateUtils.now());
        signupDao.updateById(signup);
        log.info("活动签到成功，id={}", id);
        return signup;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelSignup(Long id) {
        ActivitySignup signup = signupDao.selectById(id);
        if (signup == null || signup.getDeleted() == 1) {
            throw new BusinessException("报名记录不存在");
        }

        if ("cancelled".equals(signup.getStatus())) {
            throw new BusinessException("报名已取消");
        }

        if ("signed_in".equals(signup.getStatus())) {
            throw new BusinessException("已签到的报名不能取消");
        }

        signup.setStatus("cancelled");
        signup.setUpdateTime(DateUtils.now());
        signupDao.updateById(signup);
        log.info("取消活动报名成功，id={}", id);
    }

    /**
     * 检测报名时间冲突（同一村民同一时间段只能报名一个活动）
     */
    private void checkSignupConflict(Long activityId, Long residentId) {
        // 获取该村民在相同时间段的所有报名
        List<PublicActivity> activities = activityDao.selectList(
                new LambdaQueryWrapper<PublicActivity>()
                        .eq(PublicActivity::getDeleted, 0)
                        .ne(PublicActivity::getStatus, "archived")
        );

        for (PublicActivity activity : activities) {
            LambdaQueryWrapper<ActivitySignup> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(ActivitySignup::getResidentId, residentId);
            wrapper.eq(ActivitySignup::getActivityId, activity.getId());
            wrapper.eq(ActivitySignup::getDeleted, 0);
            wrapper.ne(ActivitySignup::getStatus, "cancelled");

            Long count = signupDao.selectCount(wrapper);
            if (count > 0) {
                throw new BusinessException("该村民已报名参加活动：" + activity.getName());
            }
        }
    }
}