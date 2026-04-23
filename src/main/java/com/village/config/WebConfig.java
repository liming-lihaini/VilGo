package com.village.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${app.attachment.path:D:/village-data/attachments}")
    private String attachmentPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 附件静态资源访问
        registry.addResourceHandler("/attachments/**")
                .addResourceLocations("file:" + attachmentPath + "/");
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}