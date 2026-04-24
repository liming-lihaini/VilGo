package com.village.service;

import com.village.dto.ActivityStatisticsDTO;
import com.village.dto.ActivitySignupDTO;
import com.village.dto.ActivitySignupQueryDTO;
import com.village.dto.PublicActivityDTO;
import com.village.dto.PublicActivityQueryDTO;
import com.village.entity.ActivitySignup;
import com.village.entity.PublicActivity;

import java.util.List;

/**
 * 公益活动服务接口
 */
public interface PublicActivityService {

    PublicActivity create(PublicActivityDTO dto);

    PublicActivity update(PublicActivityDTO dto);

    void delete(Long id);

    PublicActivity getById(Long id);

    List<PublicActivity> list(PublicActivityQueryDTO dto);

    ActivityStatisticsDTO statistics(PublicActivityQueryDTO dto);
}