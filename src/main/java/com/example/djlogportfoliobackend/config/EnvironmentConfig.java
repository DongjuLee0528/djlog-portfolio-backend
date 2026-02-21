package com.example.djlogportfoliobackend.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * 환경 변수 검증 및 로깅 설정 클래스
 *
 * 애플리케이션 시작 시 중요한 환경 변수들이 제대로 설정되었는지 확인하고
 * 로그를 통해 설정 상태를 출력한다.
 *
 * 주요 기능:
 * - 데이터베이스 연결 정보 검증
 * - 관리자 계정 설정 확인
 * - 시스템 환경 변수와 애플리케이션 프로퍼티 비교
 */
@Slf4j
@Component
public class EnvironmentConfig {

    @Value("${DB_URL:NOT_SET}")
    private String dbUrl;

    @Value("${DB_USERNAME:NOT_SET}")
    private String dbUsername;

    @Value("${admin.email:NOT_SET}")
    private String adminEmail;

    /**
     * 애플리케이션 시작 후 환경 변수 검증 및 로그 출력
     * 중요한 설정값들이 제대로 설정되었는지 확인하고 로그로 기록한다.
     * 시스템 환경변수와 스프링 프로퍼티 값을 모두 확인하여 설정 불일치를 감지할 수 있다.
     */
    @PostConstruct
    public void logEnvironmentVariables() {
        log.info("[ENV_CHECK] DB_URL: {}", dbUrl);
        log.info("[ENV_CHECK] DB_USERNAME: {}", dbUsername);
        log.info("[ENV_CHECK] admin.email: {}", adminEmail);

        // System environment variables check
        log.info("[SYS_ENV] DB_URL: {}", System.getenv("DB_URL"));
        log.info("[SYS_ENV] DB_USERNAME: {}", System.getenv("DB_USERNAME"));
        log.info("[SYS_ENV] ADMIN_USERNAME: {}", System.getenv("ADMIN_USERNAME"));
    }
}