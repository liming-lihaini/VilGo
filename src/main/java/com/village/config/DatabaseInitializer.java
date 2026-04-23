package com.village.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 数据库初始化启动器
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class DatabaseInitializer {

    private final DatabaseConfig databaseConfig;

    @Bean
    public ApplicationRunner runInitializer() {
        return args -> {
            log.info("执行数据库初始化...");
            databaseConfig.init();
        };
    }
}