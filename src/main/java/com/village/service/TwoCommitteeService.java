package com.village.service;

import com.village.dto.TwoCommitteeDTO;
import com.village.dto.TwoCommitteeQueryDTO;
import com.village.entity.TwoCommittee;

import java.util.List;
import java.util.Map;

/**
 * 两委班子服务接口
 */
public interface TwoCommitteeService {

    TwoCommittee create(TwoCommitteeDTO dto);

    TwoCommittee update(TwoCommitteeDTO dto);

    void delete(Long id);

    TwoCommittee getById(Long id);

    List<TwoCommittee> list(TwoCommitteeQueryDTO dto);

    Map<String, Long> statistics();
}
