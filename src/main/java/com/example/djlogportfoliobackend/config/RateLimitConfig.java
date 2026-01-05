package com.example.djlogportfoliobackend.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * Rate Limiting 캐시 설정
 * IP별 요청 빈도 제한을 위한 Caffeine 캐시를 구성합니다.
 * 메모리 기반 캐시로 빠른 성능과 1분 만료 정책을 제공합니다.
 */
@Configuration
public class RateLimitConfig {

    /**
     * Rate Limiting을 위한 Caffeine 캐시 빈 생성
     * IP 주소별 요청 카운터를 저장하고 관리하는 캐시를 생성합니다.
     *
     * 캐시 설정:
     * - 최대 크기: 10,000개 엔트리
     * - 만료 시간: 1분 (쓰기 기준)
     *
     * @return IP별 요청 카운터를 저장하는 캐시
     */
    @Bean
    public Cache<String, Integer> rateLimitCache() {
        return Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(Duration.ofMinutes(1))
                .build();
    }
}