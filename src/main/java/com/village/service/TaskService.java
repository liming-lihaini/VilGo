package com.village.service;

import com.village.dto.TaskDTO;
import com.village.dto.TaskQueryDTO;
import com.village.entity.Task;

import java.util.List;

/**
 * 任务服务接口
 */
public interface TaskService {

    Task create(TaskDTO dto);

    Task update(TaskDTO dto);

    void delete(Long id);

    Task getById(Long id);

    List<Task> list(TaskQueryDTO dto);
}
