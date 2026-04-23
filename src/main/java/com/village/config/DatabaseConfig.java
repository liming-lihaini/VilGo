package com.village.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 数据库配置
 * 负责数据库目录和表创建
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.attachment.path}")
    private String attachmentPath;

    /**
     * 启动时创建数据库目录和表
     */
    @PostConstruct
    public void init() {
        // ====================== 修复点：去掉 ? 后面的参数 ======================
        String dbPath = jdbcUrl.replace("jdbc:sqlite:", "");
        // 截断 URL 参数
        if (dbPath.contains("?")) {
            dbPath = dbPath.substring(0, dbPath.indexOf("?"));
        }
        // ====================================================================

        Path path = Paths.get(dbPath);
        Path parentDir = path.getParent();

        // 创建数据库目录
        if (parentDir != null && !Files.exists(parentDir)) {
            try {
                Files.createDirectories(parentDir);
                log.info("创建数据库目录: {}", parentDir);
            } catch (Exception e) {
                log.error("创建数据库目录失败: {}", parentDir, e);
                throw new RuntimeException("创建数据库目录失败: " + parentDir, e);
            }
        }

        // 创建附件目录
        Path attachmentDir = Paths.get(attachmentPath);
        if (!Files.exists(attachmentDir)) {
            try {
                Files.createDirectories(attachmentDir);
                log.info("创建附件目录: {}", attachmentPath);
            } catch (Exception e) {
                log.error("创建附件目录失败: {}", attachmentPath, e);
                throw new RuntimeException("创建附件目录失败: " + attachmentPath, e);
            }
        }

        // 检查数据库文件是否存在
        File dbFile = new File(dbPath);

        // 判断是否需要初始化（数据库不存在 或 表不存在）
        boolean needInit = !dbFile.exists();
        if (!needInit) {
            try {
                // 检查表是否存在
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM sqlite_master WHERE type='table' AND name='residents'",
                    Integer.class);
                needInit = (count == null || count == 0);
            } catch (Exception e) {
                needInit = true;
            }
        }

        if (needInit) {
            if (!dbFile.exists()) {
                log.info("数据库文件不存在，将创建: {}", dbPath);
                try {
                    // 创建空数据库文件
                    DriverManager.getConnection(jdbcUrl).close();
                    log.info("数据库文件创建成功");
                } catch (SQLException e) {
                    log.error("创建数据库文件失败", e);
                    throw new RuntimeException("创建数据库文件失败", e);
                }
            } else {
                log.info("数据库文件已存在，表不存在，将创建表");
            }

            // 执行建表脚本
            executeSchema();
        } else {
            log.info("数据库和表已存在，跳过初始化");
        }
    }

    /**
     * 执行建表脚本
     */
    private void executeSchema() {
        try {
            ClassPathResource resource = new ClassPathResource("schema.sql");
            String content = new String(resource.getInputStream().readAllBytes());

            // 按行分割，处理多行注释
            String[] lines = content.split("\n");
            StringBuilder sql = new StringBuilder();
            for (String line : lines) {
                String trimmed = line.trim();
                // 跳过空行和注释行
                if (StringUtils.hasText(trimmed) && !trimmed.startsWith("--")) {
                    sql.append(line).append("\n");
                }
            }

            // 执行完整的 SQL（按分号分割，每个语句单独执行）
            String finalSql = sql.toString();
            if (StringUtils.hasText(finalSql)) {
                String[] statements = finalSql.split(";");
                for (String stmt : statements) {
                    if (StringUtils.hasText(stmt.trim())) {
                        log.info("执行SQL: {}", stmt.trim());
                        jdbcTemplate.execute(stmt.trim());
                    }
                }
                log.info("数据库表初始化完成");
            }
        } catch (Exception e) {
            log.error("读取建表脚本失败", e);
            throw new RuntimeException("数据库初始化失败", e);
        }
    }
}