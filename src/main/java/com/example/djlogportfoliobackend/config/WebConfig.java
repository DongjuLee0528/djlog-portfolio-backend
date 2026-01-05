package com.example.djlogportfoliobackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * 웹 MVC 설정
 * Spring MVC 설정을 커스터마이징하는 설정 클래스입니다.
 * 현재 업로드된 파일에 대한 정적 리소스 핸들러를 구성합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /** 파일 업로드 디렉토리 경로 */
    @Value("${file.upload.dir}")
    private String uploadDir;

    /**
     * 정적 리소스 핸들러 설정
     * 업로드된 파일에 대한 웹 액세스를 제공하기 위한 리소스 핸들러를 추가합니다.
     * '/uploads/**' 경로로 들어오는 요청을 실제 파일 시스템의 업로드 디렉토리와 매핑합니다.
     *
     * 예시: GET /uploads/image.jpg -> {uploadDir}/image.jpg 파일 제공
     *
     * @param registry 리소스 핸들러 레지스트리
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize().toString();

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}