package com.village.service;

import com.village.dto.SpecialGroupDTO;
import com.village.dto.SpecialGroupDetailDTO;
import com.village.dto.SpecialGroupQueryDTO;
import com.village.entity.SpecialGroup;
import java.util.List;
import java.util.Map;

/**
 * 特殊人群服务接口
 */
public interface SpecialGroupService {

    SpecialGroup create(SpecialGroupDTO dto);

    SpecialGroup update(SpecialGroupDTO dto);

    void delete(Long id);

    SpecialGroupDetailDTO getDetail(Long id);

    List<SpecialGroup> list(SpecialGroupQueryDTO dto);

    List<SpecialGroup> listForExport(SpecialGroupQueryDTO dto);

    Map<String, Long> statistics();

    List<SpecialGroup> getRemindList();
}