package com.village.util;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.village.dto.ResidentDTO;
import com.village.dto.ResidentImportResultDTO;
import com.village.entity.Resident;
import com.village.service.ResidentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入监听器
 */
@Slf4j
@RequiredArgsConstructor
public class ResidentImportListener implements ReadListener<Resident> {

    private final ResidentService residentService;
    private final ResidentImportResultDTO resultDTO;

    /**
     * 批量插入大小
     */
    private static final int BATCH_SIZE = 100;

    /**
     * 缓存的数据
     */
    private final List<Resident> cachedDataList = new ArrayList<>();

    @Override
    public void invoke(Resident data, AnalysisContext context) {
        int rowIndex = context.readRowHolder().getRowIndex() + 1;

        // 校验必填字段
        StringBuilder errorMsg = new StringBuilder();

        if (data.getName() == null || data.getName().trim().isEmpty()) {
            errorMsg.append("姓名不能为空;");
        }
        if (data.getIdCard() == null || data.getIdCard().trim().isEmpty()) {
            errorMsg.append("身份证号不能为空;");
        } else if (data.getIdCard().length() != 18) {
            errorMsg.append("身份证号必须为18位;");
        }

        if (errorMsg.length() > 0) {
            resultDTO.addError(rowIndex, errorMsg.toString(),
                    data.getName() + "," + data.getIdCard());
            return;
        }

        // 设置默认值
        if (data.getGender() == null || data.getGender().trim().isEmpty()) {
            data.setGender("male");
        }
        if (data.getHouseholdStatus() == null || data.getHouseholdStatus().trim().isEmpty()) {
            data.setHouseholdStatus("normal");
        }
        if (data.getIsLocalHousehold() == null) {
            data.setIsLocalHousehold(1);
        }
        if (data.getIsHouseholdHead() == null) {
            data.setIsHouseholdHead(0);
        }
        if (data.getIsLocalResident() == null) {
            data.setIsLocalResident(1);
        }

        cachedDataList.add(data);

        // 达到批量大小则插入
        if (cachedDataList.size() >= BATCH_SIZE) {
            saveData();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 最后保存剩余的数据
        saveData();
    }

    /**
     * 批量保存数据
     */
    private void saveData() {
        if (cachedDataList.isEmpty()) {
            return;
        }

        try {
            // 逐条检查是否存在并插入
            for (Resident resident : cachedDataList) {
                try {
                    // 转换为DTO
                    ResidentDTO dto = convertToDTO(resident);
                    residentService.create(dto);
                    resultDTO.setSuccessCount(resultDTO.getSuccessCount() + 1);
                } catch (Exception e) {
                    // 身份证号重复或其他错误
                    log.warn("导入失败: {}", e.getMessage());
                    resultDTO.addError(0, e.getMessage(),
                            resident.getName() + "," + resident.getIdCard());
                }
            }
        } catch (Exception e) {
            log.error("批量保存失败", e);
        } finally {
            cachedDataList.clear();
        }

        resultDTO.setTotalCount(resultDTO.getSuccessCount() + resultDTO.getFailCount());
    }

    /**
     * Resident转换为ResidentDTO
     */
    private ResidentDTO convertToDTO(Resident resident) {
        ResidentDTO dto = new ResidentDTO();
        dto.setName(resident.getName());
        dto.setIdCard(resident.getIdCard());
        dto.setGender(resident.getGender());
        dto.setBirthDate(resident.getBirthDate());
        dto.setPhone(resident.getPhone());
        dto.setAddress(resident.getAddress());
        dto.setPersonType(resident.getPersonType());
        dto.setSecurityType(resident.getSecurityType());
        dto.setHouseholdHead(resident.getHouseholdHead());
        dto.setRelationship(resident.getRelationship());
        dto.setVillage(resident.getVillage());
        dto.setIsLocalHousehold(resident.getIsLocalHousehold());
        dto.setIsHouseholdHead(resident.getIsHouseholdHead());
        dto.setIsLocalResident(resident.getIsLocalResident());
        dto.setExternalAddress(resident.getExternalAddress());
        dto.setRemark(resident.getRemark());
        return dto;
    }
}