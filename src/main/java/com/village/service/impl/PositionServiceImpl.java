package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.village.dao.PositionDao;
import com.village.dto.PositionDTO;
import com.village.entity.Position;
import com.village.exception.BusinessException;
import com.village.service.PositionService;
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
public class PositionServiceImpl implements PositionService {

    private final PositionDao positionDao;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Position create(PositionDTO dto) {
        if (!StringUtils.hasText(dto.getName())) {
            throw new BusinessException("职位名称不能为空");
        }

        Position position = new Position();
        position.setName(dto.getName());
        position.setCode(dto.getCode());
        position.setSortOrder(dto.getSortOrder() != null ? dto.getSortOrder() : 0);
        position.setCreateTime(DateUtils.now());
        position.setUpdateTime(DateUtils.now());
        position.setDeleted(0);

        positionDao.insert(position);
        log.info("新增职位成功，id={}", position.getId());
        return position;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Position update(PositionDTO dto) {
        if (dto.getId() == null) {
            throw new BusinessException("职位ID不能为空");
        }

        Position position = positionDao.selectById(dto.getId());
        if (position == null || position.getDeleted() == 1) {
            throw new BusinessException("职位不存在");
        }

        if (StringUtils.hasText(dto.getName())) {
            position.setName(dto.getName());
        }
        position.setCode(dto.getCode());
        if (dto.getSortOrder() != null) {
            position.setSortOrder(dto.getSortOrder());
        }
        position.setUpdateTime(DateUtils.now());

        positionDao.updateById(position);
        log.info("更新职位成功，id={}", dto.getId());
        return position;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        Position position = positionDao.selectById(id);
        if (position == null || position.getDeleted() == 1) {
            throw new BusinessException("职位不存在");
        }

        position.setDeleted(1);
        position.setUpdateTime(DateUtils.now());
        positionDao.updateById(position);
        log.info("删除职位成功，id={}", id);
    }

    @Override
    public Position getById(Long id) {
        return positionDao.selectById(id);
    }

    @Override
    public List<Position> list() {
        LambdaQueryWrapper<Position> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Position::getDeleted, 0);
        wrapper.orderByAsc(Position::getSortOrder);
        wrapper.orderByDesc(Position::getCreateTime);
        return positionDao.selectList(wrapper);
    }
}
