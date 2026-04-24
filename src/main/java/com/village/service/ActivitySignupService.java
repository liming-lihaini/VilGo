package com.village.service;

import com.village.dto.ActivitySignupDTO;
import com.village.dto.ActivitySignupQueryDTO;
import com.village.entity.ActivitySignup;

import java.util.List;

/**
 * 活动报名服务接口
 */
public interface ActivitySignupService {

    ActivitySignup create(ActivitySignupDTO dto);

    ActivitySignup update(ActivitySignupDTO dto);

    void delete(Long id);

    ActivitySignup getById(Long id);

    List<ActivitySignup> list(ActivitySignupQueryDTO dto);

    ActivitySignup signIn(Long id);

    void cancelSignup(Long id);
}