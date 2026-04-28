package com.village.controller;

import com.village.dto.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件上传控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB

    // 上传文件保存目录
    private static final String UPLOAD_DIR = "D:/village-data/attachments/news";

    /**
     * 上传图片
     */
    @PostMapping("/image")
    public Result<String> uploadImage(@RequestParam("file") MultipartFile file) {
        log.info("上传图片，filename={}", file.getOriginalFilename());

        // 校验文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            return Result.error("图片大小不能超过10MB");
        }

        // 校验文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isEmpty()) {
            return Result.error("文件名不能为空");
        }

        String ext = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        boolean allowed = false;
        for (String allowedExt : ALLOWED_EXTENSIONS) {
            if (allowedExt.equals(ext)) {
                allowed = true;
                break;
            }
        }
        if (!allowed) {
            return Result.error("不支持的图片格式，仅支持jpg、jpeg、png、gif、webp");
        }

        try {
            // 生成保存路径
            String datePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
            String saveDir = UPLOAD_DIR + "/" + datePath;

            // 创建目录
            File dir = new File(saveDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            // 生成唯一文件名
            String newFilename = UUID.randomUUID().toString().replace("-", "") + ext;
            File targetFile = new File(saveDir, newFilename);

            // 保存文件
            file.transferTo(targetFile);

            // 返回相对路径
            String relativePath = "/attachments/news/" + datePath + "/" + newFilename;
            log.info("图片上传成功，path={}", relativePath);

            return Result.success(relativePath);
        } catch (IOException e) {
            log.error("图片上传失败", e);
            return Result.error("图片上传失败: " + e.getMessage());
        }
    }
}
