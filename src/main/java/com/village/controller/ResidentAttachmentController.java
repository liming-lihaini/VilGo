package com.village.controller;

import com.village.dto.Result;
import com.village.entity.ResidentAttachment;
import com.village.service.ResidentAttachmentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * 村民附件控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/resident/attachment")
@RequiredArgsConstructor
public class ResidentAttachmentController {

    private final ResidentAttachmentService attachmentService;

    /**
     * 上传附件
     */
    @PostMapping("/upload")
    public Result<ResidentAttachment> upload(
            @RequestParam Long residentId,
            @RequestParam MultipartFile file,
            @RequestParam String fileCategory) throws IOException {
        log.info("上传附件，residentId={}, fileCategory={}", residentId, fileCategory);
        ResidentAttachment attachment = attachmentService.upload(residentId, file, fileCategory);
        return Result.success(attachment);
    }

    /**
     * 获取村民附件列表
     */
    @GetMapping("/list/{residentId}")
    public Result<List<ResidentAttachment>> list(@PathVariable Long residentId) {
        log.info("获取附件列表，residentId={}", residentId);
        List<ResidentAttachment> list = attachmentService.listByResidentId(residentId);
        return Result.success(list);
    }

    /**
     * 删除附件
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        log.info("删除附件，id={}", id);
        attachmentService.delete(id);
        return Result.success();
    }

    /**
     * 下载附件
     */
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> download(@PathVariable Long id) throws MalformedURLException {
        List<ResidentAttachment> attachments = attachmentService.listByResidentId(null);
        ResidentAttachment attachment = attachments.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst()
                .orElse(null);

        if (attachment == null) {
            return ResponseEntity.notFound().build();
        }

        String basePath = "D:/village-data/attachments";
        Path filePath = Paths.get(basePath + attachment.getFilePath().replace("/attachments", ""));
        Resource resource = new UrlResource(filePath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(attachment.getFileType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getFileName() + "\"")
                .body(resource);
    }
}
