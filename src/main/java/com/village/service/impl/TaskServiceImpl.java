package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.village.dao.TaskDao;
import com.village.dto.TaskDTO;
import com.village.dto.TaskQueryDTO;
import com.village.entity.Task;
import com.village.exception.BusinessException;
import com.village.service.TaskService;
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
public class TaskServiceImpl implements TaskService {

    private final TaskDao taskDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task create(TaskDTO dto) {
        if (dto.getAssigneeId() == null) {
            throw new BusinessException("接收人不能为空");
        }

        Task task = new Task();
        task.setYear(dto.getYear());
        task.setAssigner(dto.getAssigner());
        task.setAssigneeId(dto.getAssigneeId());
        task.setContent(dto.getContent());
        task.setDeadline(dto.getDeadline());
        task.setStatus("pending");
        task.setResult(dto.getResult());
        task.setCreateTime(DateUtils.now());
        task.setUpdateTime(DateUtils.now());
        task.setDeleted(0);

        taskDao.insert(task);
        log.info("新增任务成功，id={}", task.getId());
        return task;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Task update(TaskDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("任务ID不能为空");
        }

        Task task = taskDao.selectById(dto.getId());
        if (task == null || task.getDeleted() == 1) {
            throw new BusinessException("任务不存在");
        }

        if (StringUtils.hasText(dto.getStatus())) {
            String oldStatus = task.getStatus();
            String newStatus = dto.getStatus();
            if (!isValidStatusTransition(oldStatus, newStatus)) {
                throw new BusinessException("状态流转不合理");
            }
            task.setStatus(newStatus);
        }

        task.setContent(dto.getContent());
        task.setDeadline(dto.getDeadline());
        task.setResult(dto.getResult());
        task.setUpdateTime(DateUtils.now());

        taskDao.updateById(task);
        log.info("更新任务成功，id={}", dto.getId());
        return task;
    }

    private boolean isValidStatusTransition(String oldStatus, String newStatus) {
        if ("pending".equals(oldStatus)) {
            return "pending".equals(newStatus) || "ongoing".equals(newStatus);
        }
        if ("ongoing".equals(oldStatus)) {
            return "ongoing".equals(newStatus) || "completed".equals(newStatus);
        }
        if ("completed".equals(oldStatus)) {
            return "completed".equals(newStatus);
        }
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Task task = taskDao.selectById(id);
        if (task == null || task.getDeleted() == 1) {
            throw new BusinessException("任务不存在");
        }

        task.setDeleted(1);
        task.setUpdateTime(DateUtils.now());
        taskDao.updateById(task);
        log.info("删除任务成功，id={}", id);
    }

    @Override
    public Task getById(Long id) {
        return taskDao.selectById(id);
    }

    @Override
    public List<Task> list(TaskQueryDTO dto) {
        LambdaQueryWrapper<Task> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Task::getDeleted, 0);

        if (dto.getAssigneeId() != null) {
            wrapper.eq(Task::getAssigneeId, dto.getAssigneeId());
        }
        if (StringUtils.hasText(dto.getStatus())) {
            wrapper.eq(Task::getStatus, dto.getStatus());
        }
        if (dto.getYear() != null) {
            wrapper.eq(Task::getYear, dto.getYear());
        }

        wrapper.orderByDesc(Task::getCreateTime);

        Page<Task> page = new Page<>(dto.getPageNum(), dto.getPageSize());
        IPage<Task> result = taskDao.selectPage(page, wrapper);
        return result.getRecords();
    }
}
