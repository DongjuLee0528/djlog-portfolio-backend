package com.example.djlogportfoliobackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 보안 헤더 설정 클래스
 *
 * 웹 애플리케이션의 보안성을 강화하기 위해 다양한 HTTP 보안 헤더를 자동으로 추가한다.
 *
 * 설정되는 보안 헤더:
 * - X-Content-Type-Options: MIME 스니핑 공격 방지
 * - X-Frame-Options: 클릭재킹 공격 방지
 * - X-XSS-Protection: XSS 공격 차단
 * - Content-Security-Policy: 콘텐츠 보안 정책
 * - Strict-Transport-Security: HTTPS 강제 사용 (HTTPS 환경에서만)
 * - Permissions-Policy: 브라우저 API 접근 제한
 */
@Configuration
public class SecurityHeadersConfig {

    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {

                // X-Content-Type-Options
                response.setHeader("X-Content-Type-Options", "nosniff");

                // X-Frame-Options
                response.setHeader("X-Frame-Options", "DENY");

                // X-XSS-Protection
                response.setHeader("X-XSS-Protection", "1; mode=block");

                // Referrer-Policy
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

                // Content-Security-Policy
                response.setHeader("Content-Security-Policy",
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data: https:; " +
                    "font-src 'self' data:; " +
                    "connect-src 'self'; " +
                    "frame-ancestors 'none'");

                // Strict-Transport-Security (HSTS) - only for HTTPS
                if (request.isSecure()) {
                    response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                }

                // Permissions-Policy
                response.setHeader("Permissions-Policy",
                    "camera=(), " +
                    "microphone=(), " +
                    "geolocation=(), " +
                    "payment=(), " +
                    "usb=(), " +
                    "magnetometer=(), " +
                    "gyroscope=(), " +
                    "accelerometer=()");

                filterChain.doFilter(request, response);
            }
        };
    }
}