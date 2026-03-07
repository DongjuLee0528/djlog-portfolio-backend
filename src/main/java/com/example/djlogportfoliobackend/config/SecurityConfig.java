package com.example.djlogportfoliobackend.config;

import com.example.djlogportfoliobackend.filter.JwtAuthenticationFilter;
import com.example.djlogportfoliobackend.filter.RateLimitFilter;
import com.example.djlogportfoliobackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Spring Security 보안 설정 클래스
 *
 * 포트폴리오 백엔드 애플리케이션의 전반적인 보안 정책을 구성합니다.
 * JWT 기반 인증, CORS 설정, API 엔드포인트 접근 권한 관리 등을 담당합니다.
 *
 * <h3>주요 기능</h3>
 * <ul>
 *   <li><strong>JWT 인증</strong>: 무상태(Stateless) JWT 토큰 기반 인증</li>
 *   <li><strong>CORS 설정</strong>: 프론트엔드와의 크로스 도메인 통신 허용</li>
 *   <li><strong>API 접근 제어</strong>: 공개/비공개 엔드포인트 구분</li>
 *   <li><strong>보안 필터 체인</strong>: 보안 헤더, 속도 제한, JWT 검증 순차 적용</li>
 * </ul>
 *
 * <h3>보안 정책</h3>
 * <ul>
 *   <li>공개 데이터 조회: GET 요청만 허용 (프로필, 프로젝트, 교육, 자격증, 스킬)</li>
 *   <li>관리자 기능: JWT 토큰 인증 필수 (생성, 수정, 삭제)</li>
 *   <li>로그인: 인증 없이 접근 가능</li>
 *   <li>파일 업로드: 업로드된 파일 public 접근 허용</li>
 * </ul>
 *
 * @author djlog
 * @version 1.0
 * @since 1.0
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final SecurityHeadersConfig securityHeadersConfig;
    private final RateLimitFilter rateLimitFilter;

    @Value("${security.cors.allowed-origins:https://djloghub.com,https://www.djloghub.com}")
    private String allowedOrigins;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        // 공개 데이터 조회 엔드포인트 화이트리스트 (GET 요청만 허용)
                        .requestMatchers(HttpMethod.GET, "/api/profile").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/projects/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/educations/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/certificates/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/skills/**").permitAll()
                        .requestMatchers("/uploads/**").permitAll()
                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(securityHeadersConfig.securityHeadersFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        List<String> origins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());

        configuration.setAllowedOriginPatterns(origins);
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList(
                "Authorization",
                "Content-Type",
                "X-Requested-With",
                "Accept",
                "Origin",
                "Access-Control-Request-Method",
                "Access-Control-Request-Headers"
        ));
        configuration.setExposedHeaders(Arrays.asList(
                "Access-Control-Allow-Origin",
                "Access-Control-Allow-Credentials"
        ));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}