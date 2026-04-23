package com.village.dto;

import lombok.Data;
import java.util.ArrayList;
import java.util.List;

/**
 * Excel导入结果 DTO
 */
@Data
public class ResidentImportResultDTO {

    /**
     * 成功数量
     */
    private int successCount;

    /**
     * 失败数量
     */
    private int failCount;

    /**
     * 总数量
     */
    private int totalCount;

    /**
     * 错误详情
     */
    private List<ImportError> errors = new ArrayList<>();

    @Data
    public static class ImportError {
        /**
         * 行号
         */
        private int rowNum;

        /**
         * 错误原因
         */
        private String reason;

        /**
         * 原始数据
         */
        private String originalData;

        public ImportError(int rowNum, String reason, String originalData) {
            this.rowNum = rowNum;
            this.reason = reason;
            this.originalData = originalData;
        }
    }

    public void addError(int rowNum, String reason, String originalData) {
        this.errors.add(new ImportError(rowNum, reason, originalData));
        this.failCount++;
    }
}