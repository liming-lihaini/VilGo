package com.village.service;

import com.village.dto.ResidentDTO;
import com.village.dto.ResidentQueryDTO;
import com.village.dto.ResidentStatisticsDTO;
import com.village.entity.Resident;
import java.util.List;

/**
 * 村民档案服务接口
 */
public interface ResidentService {

    /**
     * 新增村民档案
     */
    Resident create(ResidentDTO dto);

    /**
     * 更新村民档案
     */
    Resident update(ResidentDTO dto);

    /**
     * 删除村民档案（软删除）
     */
    void delete(Long id);

    /**
     * 获取村民档案
     */
    Resident getById(Long id);

    /**
     * 查询村民列表
     */
    List<Resident> list(ResidentQueryDTO dto);

    /**
     * 销户登记
     */
    void cancel(Long id);

    /**
     * 人口统计
     */
    ResidentStatisticsDTO statistics();

    /**
     * 导出Excel
     */
    List<Resident> listForExport(ResidentQueryDTO dto);
}