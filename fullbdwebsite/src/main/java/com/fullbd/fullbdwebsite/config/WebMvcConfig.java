package com.fullbd.fullbdwebsite.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // Lấy giá trị từ file properties
    @Value("${app.upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path path = Paths.get(uploadDir);
        String uploadPath = path.toFile().getAbsolutePath();

        registry.addResourceHandler("/project-images/**")
                .addResourceLocations("file:/" + uploadPath + "/");
    }
}