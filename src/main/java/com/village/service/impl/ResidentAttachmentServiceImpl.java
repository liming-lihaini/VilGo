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
import org.springframework.util.unit.DataSize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ResidentAttachmentServiceImpl implements ResidentAttachmentService {

    private final ResidentAttachmentDao attachmentDao;

    @Value("${app.attachment.path:D:/village-data/attachments}")
    private String attachmentPath;

    @Value("${app.attachment.max-size:50MB}")
    private String maxSizeStr;

    /**
     * 上传附件（修复版）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResidentAttachment upload(Long residentId, MultipartFile file, String fileCategory) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("文件不能为空");
        }

        // 限制文件大小
        long maxBytes = DataSize.parse(maxSizeStr).toBytes();
        if (file.getSize() > maxBytes) {
            throw new BusinessException("文件大小不能超过" + maxSizeStr);
        }

        // 基础目录
        File baseDir = new File(attachmentPath);
        if (!baseDir.exists()) {
            boolean mkdir = baseDir.mkdirs();
            if (!mkdir) {
                throw new BusinessException("附件目录创建失败");
            }
        }

        // 村民子目录
        File residentDir = new File(baseDir, String.valueOf(residentId));
        if (!residentDir.exists() && !residentDir.mkdirs()) {
            throw new BusinessException("村民附件目录创建失败");
        }

        // 文件名处理
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            originalFilename = "unknown";
        }

        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        String newFileName = UUID.randomUUID() + extension;
        File destFile = new File(residentDir, newFileName);

        // 保存文件（推荐写法）
        try {
            Files.copy(file.getInputStream(), destFile.toPath());
        } catch (Exception e) {
            log.error("文件保存失败", e);
            throw new BusinessException("文件上传失败：" + e.getMessage());
        }

        // 入库
        ResidentAttachment attachment = new ResidentAttachment();
        attachment.setResidentId(residentId);
        attachment.setFileName(originalFilename);
        attachment.setFilePath("/attachments/" + residentId + "/" + newFileName);
        attachment.setFileType(file.getContentType());
        attachment.setFileSize(file.getSize());
        attachment.setFileCategory(fileCategory);
        attachment.setCreateTime(DateUtils.now());
        attachment.setDeleted(0);

        attachmentDao.insert(attachment);
        log.info("上传附件成功 id={} fileName={}", attachment.getId(), originalFilename);

        return attachment;
    }

    /**
     * 查询附件列表
     */
    @Override
    public List<ResidentAttachment> listByResidentId(Long residentId) {
        LambdaQueryWrapper<ResidentAttachment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(ResidentAttachment::getResidentId, residentId)
               .eq(ResidentAttachment::getDeleted, 0)
               .orderByDesc(ResidentAttachment::getCreateTime);
        return attachmentDao.selectList(wrapper);
    }

    /**
     * 删除附件（修复路径错误 + 异常安全）
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        ResidentAttachment attachment = attachmentDao.selectById(id);
        if (attachment == null) {
            throw new BusinessException("附件不存在");
        }

        // ======================= 修复路径错误 =======================
        String filePath = attachment.getFilePath();
        if (!filePath.startsWith("/attachments/")) {
            throw new BusinessException("附件路径格式错误");
        }

        // 正确拼接：去掉 /attachments
        String relativePath = filePath.substring("/attachments".length());
        File file = new File(attachmentPath, relativePath);

        // 删除物理文件（异常只打日志，不影响数据库删除）
        try {
            if (file.exists()) {
                Files.deleteIfExists(file.toPath());
                log.info("删除物理文件成功：{}", file.getAbsolutePath());
            }
        } catch (Exception e) {
            log.error("删除物理文件失败：{}", file.getAbsolutePath(), e);
            // 不抛出异常，保证数据库软删除成功
        }

        // 软删除
        attachment.setDeleted(1);
        attachmentDao.deleteById(id);
        log.info("附件软删除成功 id={}", id);
    }
}