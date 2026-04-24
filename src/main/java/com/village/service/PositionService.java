package com.village.service;

import com.village.dto.PositionDTO;
import com.village.entity.Position;

import java.util.List;

/**
 * 职位服务接口
 */
public interface PositionService {

    Position create(PositionDTO dto);

    Position update(PositionDTO dto);

    void delete(Long id);

    Position getById(Long id);

    List<Position> list();
}
