package com.example.djlogportfoliobackend.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Rate Limiting 캐시 설정
 * 다양한 전략(IP, IP+URI, 글로벌)의 요청 빈도 제한을 위한 Caffeine 캐시를 구성합니다.
 * AtomicInteger를 사용하여 thread-safe한 카운터를 제공하고, 1분 만료 정책을 적용합니다.
 */
@Configuration
public class RateLimitConfig {

    /**
     * Rate Limiting을 위한 Caffeine 캐시 빈 생성
     * 다양한 키 전략(IP, IP+URI, 글로벌)에 따른 AtomicInteger 카운터를 저장하고 관리합니다.
     *
     * 캐시 설정:
     * - 최대 크기: 50,000개 엔트리 (더 많은 IP+URI 조합 지원)
     * - 만료 시간: 1분 (쓰기 기준, Fixed Window)
     * - AtomicInteger: Thread-safe한 카운트 연산 보장
     *
     * @return 요청 카운터를 저장하는 캐시
     */
    @Bean
    public Cache<String, AtomicInteger> rateLimitCache() {
        return Caffeine.newBuilder()
                .maximumSize(50000) // IP+URI 조합을 위해 증가
                .expireAfterWrite(Duration.ofMinutes(1)) // Fixed Window: 1분
                .recordStats() // 모니터링을 위한 통계 수집
                .build();
    }
}