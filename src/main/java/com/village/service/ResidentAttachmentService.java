package com.village.service;

import com.village.entity.ResidentAttachment;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * 村民附件服务接口
 */
public interface ResidentAttachmentService {

    /**
     * 上传附件
     */
    ResidentAttachment upload(Long residentId, MultipartFile file, String fileCategory) throws IOException;

    /**
     * 获取村民附件列表
     */
    List<ResidentAttachment> listByResidentId(Long residentId);

    /**
     * 删除附件
     */
    void delete(Long id);
}
