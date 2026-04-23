package com.village.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * 村民附件 DTO
 */
@Data
public class ResidentAttachmentDTO {

    /**
     * 村民ID
     */
    private Long residentId;

    /**
     * 文件
     */
    private MultipartFile file;

    /**
     * 文件分类：id_card-身份证照片、personal_photo-个人照片、copy-复印件
     */
    private String fileCategory;
}
