package com.example.auction.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Value("${file.dir}")
    private String fileDir;

    /**
     * CORS 허용 설정
     */
    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        // API 요청
        corsRegistry.addMapping("/api/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowCredentials(true)
                .allowedHeaders("*");
//                .exposedHeaders("Set-Cookie", "Authorization")

        // 정적 파일 요청
        corsRegistry.addMapping("/files/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET")
                .allowCredentials(true)
                .allowedHeaders("*");
    }

    /**
     * 정적 파일 요청에 대해 실제 로컬 디스크와 연결
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + fileDir + "/");
    }
}
