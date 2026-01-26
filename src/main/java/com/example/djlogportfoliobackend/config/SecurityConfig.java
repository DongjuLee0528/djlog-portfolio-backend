package com.example.djlogportfoliobackend.config;

import com.example.djlogportfoliobackend.filter.JwtAuthenticationFilter;
import com.example.djlogportfoliobackend.filter.RateLimitFilter;
import com.example.djlogportfoliobackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.http.HttpMethod;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.util.Arrays;

/**
 * Spring Security 메인 설정 클래스
 *
 * JWT 기반 인증 시스템과 다층 보안 필터를 구성한다.
 * 세션을 사용하지 않는 Stateless 아키텍처로 설계되었으며,
 * 환경별 CORS 정책과 다양한 보안 헤더를 적용한다.
 *
 * 보안 필터 실행 순서:
 * 1. SecurityHeadersFilter: 모든 요청에 보안 헤더 추가
 * 2. RateLimitFilter: 요청 빈도 제한
 * 3. JwtAuthenticationFilter: JWT 토큰 검증 및 인증
 *
 * 주요 기능:
 * - JWT 기반 인증/인가
 * - 환경별 CORS 설정 (dev/staging/prod)
 * - Rate Limiting 적용
 * - 보안 헤더 자동 추가
 * - BCrypt 비밀번호 암호화
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 토큰 검증 및 파싱을 위한 유틸 클래스
    private final JwtUtil jwtUtil;

    // 보안 헤더 필터
    private final SecurityHeadersConfig securityHeadersConfig;

    // Rate Limiting 필터
    private final RateLimitFilter rateLimitFilter;

    /**
     * Spring Security의 핵심 설정
     * - JWT 기반 인증
     * - Stateless 세션
     * - CORS 정책 적용
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF 비활성화 (JWT 사용 시 필수)
                .csrf(AbstractHttpConfigurer::disable)

                // CORS 설정 적용
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 세션 사용 안 함 (JWT는 서버 세션을 사용하지 않음)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // 요청별 접근 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // 로그인 API는 인증 없이 허용
                        .requestMatchers("/api/auth/login").permitAll()

                        // ❗️모든 GET 요청 허용 → 실제 서비스에서는 위험
                        // 민감 데이터가 있다면 반드시 제한 필요
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()

                        // 업로드 파일 접근 허용
                        .requestMatchers("/uploads/**").permitAll()

                        // 그 외 요청은 JWT 인증 필요
                        .anyRequest().authenticated()
                )

                // 1. 보안 헤더 필터를 가장 먼저 실행 (모든 요청에 대해)
                .addFilterBefore(
                        securityHeadersConfig.securityHeadersFilter(),
                        RateLimitFilter.class
                )

                // 2. Rate Limiting 필터를 두 번째로 실행 (JWT 검증 전에 요청 제한)
                .addFilterBefore(
                        rateLimitFilter,
                        JwtAuthenticationFilter.class
                )

                // 3. JWT 필터를 마지막에 실행
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtUtil),
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    /**
     * CORS 설정
     * - 환경(dev / test / prod)에 따라 허용 도메인 분리
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // 현재 실행 중인 Spring profile 확인 (기본값 dev)
        String profile = System.getProperty("spring.profiles.active", "dev");

        if ("prod".equals(profile) || "production".equals(profile)) {
            // 프로덕션 환경: 실제 서비스 도메인만 허용
            configuration.setAllowedOrigins(Arrays.asList(
                    "https://djloghub.com",
                    "https://www.djloghub.com"
            ));

        } else if ("staging".equals(profile) || "test".equals(profile)) {
            // 스테이징 / 테스트 환경
            configuration.setAllowedOrigins(Arrays.asList(
                    "https://djloghub.com",
                    "https://www.djloghub.com",
                    "https://staging-djlog.vercel.app",
                    "https://test-djlog.netlify.app"
            ));

        } else {
            // 개발 환경: 로컬 환경만 허용 (보안상 와일드카드 미사용)
            configuration.setAllowedOrigins(Arrays.asList(
                    "http://localhost:3000",
                    "http://localhost:3001",
                    "http://localhost:8080",
                    "https://localhost:3000",
                    "https://localhost:3001"
            ));

            // 필요 시 패턴 허용 (권장 X)
            // configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:*"));
        }

        // 허용 HTTP 메서드
        configuration.setAllowedMethods(Arrays.asList(
                "GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"
        ));

        // 허용 요청 헤더
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));

        // 클라이언트에서 접근 가능한 응답 헤더
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));

        // 쿠키, Authorization 헤더 허용
        configuration.setAllowCredentials(true);

        // Preflight 요청 캐시 시간 (초)
        configuration.setMaxAge(3600L);

        // 모든 경로에 CORS 설정 적용
        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

    /**
     * 비밀번호 암호화 설정
     * - BCrypt는 현재 표준
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
