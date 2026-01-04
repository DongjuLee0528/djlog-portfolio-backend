package com.example.djlogportfoliobackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

@Slf4j
@Component
public class EnvironmentConfig {

    @Value("${DB_URL:NOT_SET}")
    private String dbUrl;

    @Value("${DB_USERNAME:NOT_SET}")
    private String dbUsername;

    @Value("${ADMIN_USERNAME:NOT_SET}")
    private String adminUsername;

    @PostConstruct
    public void logEnvironmentVariables() {
        log.info("[ENV_CHECK] DB_URL: {}", dbUrl);
        log.info("[ENV_CHECK] DB_USERNAME: {}", dbUsername);
        log.info("[ENV_CHECK] ADMIN_USERNAME: {}", adminUsername);

        // System environment variables check
        log.info("[SYS_ENV] DB_URL: {}", System.getenv("DB_URL"));
        log.info("[SYS_ENV] DB_USERNAME: {}", System.getenv("DB_USERNAME"));
    }
}