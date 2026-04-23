package com.village.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.village.dao.ResidentAttachmentDao;
import com.village.entity.ResidentAttachment;
import com.village.exception.BusinessException;
import com.village.service.ResidentAttachmentService;
import com.village.util.DateUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * 村民附件服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResidentAttachmentServiceImpl implements ResidentAttachmentService {

    private final ResidentAttachmentDao attachmentDao;

    @Value("${app.attachment.path:D:/village-data/attachments}")
    private String attachmentPath;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResidentAttachment upload(Long residentId, MultipartFile file, String fileCategory) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 创建存储目录
        File dir = new File(attachmentPath + "/" + residentId);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String newFileName = UUID.randomUUID().toString() + extension;
        String filePath = dir.getAbsolutePath() + "/" + newFileName;

        // 保存文件
        file.transferTo(new File(filePath));

        // 保存数据库记录
        ResidentAttachment attachment = new ResidentAttachment();
        attachment.setResidentId(residentId);
        attachment.setFileName(originalFilename);
        attachment.setFilePath("/attachments/" + residentId + "/" + newFileName);
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setFileCategory(fileCategory);
        attachment.setCreateTime(DateUtils.now());

        attachmentDao.insert(attachment);
        log.info("上传附件成功，id={}, fileName={}", attachment.getId(), originalFilename);

        return attachment;
    }

    @Override
    public List<ResidentAttachment> listByResidentId(Long residentId) {
        LambdaQueryWrapper<ResidentAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResidentAttachment::getResidentId, residentId);
        wrapper.eq(ResidentAttachment::getDeleted, 0);
        wrapper.orderByDesc(ResidentAttachment::getCreateTime);
        return attachmentDao.selectList(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ResidentAttachment attachment = attachmentDao.selectById(id);
        if (attachment == null) {
            throw new BusinessException("附件不存在");
        }

        // 删除物理文件
        String fullPath = attachmentPath + attachment.getFilePath().replace("/attachments", "");
        File file = new File(fullPath);
        if (file.exists()) {
            file.delete();
        }

        // 软删除
        attachment.setDeleted(1);
        attachmentDao.updateById(attachment);
        log.info("删除附件成功，id={}", id);
    }
}
