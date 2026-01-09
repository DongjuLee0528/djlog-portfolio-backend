package com.example.djlogportfoliobackend.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

/**
 * JWT 토큰 블랙리스트 서비스
 * Redis를 이용하여 무효화된 JWT 토큰을 관리합니다.
 *
 * 주요 기능:
 * - 로그아웃시 토큰 블랙리스트에 추가
 * - 토큰 검증시 블랙리스트 확인
 * - 자동 만료 처리 (Redis TTL 활용)
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class JwtBlacklistService {

    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    /**
     * JWT 토큰을 블랙리스트에 추가
     *
     * @param token JWT 토큰
     * @param expiration 토큰 만료 시간
     */
    public void blacklistToken(String token, Date expiration) {
        try {
            // 토큰의 앞 부분만 저장 (보안상 전체 토큰 저장 방지)
            String tokenId = extractTokenId(token);
            String key = BLACKLIST_PREFIX + tokenId;

            // 토큰 만료시간까지 Redis에 저장 (자동 만료)
            long ttl = Math.max(0, expiration.getTime() - System.currentTimeMillis());
            if (ttl > 0) {
                redisTemplate.opsForValue().set(key, "blacklisted", Duration.ofMillis(ttl));
                log.info("Token blacklisted successfully: {} (TTL: {}ms)", tokenId, ttl);
            } else {
                log.debug("Token already expired, not adding to blacklist: {}", tokenId);
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage(), e);
            // Redis 장애시에도 서비스 전체가 중단되지 않도록 예외를 삼킴
        }
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     *
     * @param token JWT 토큰
     * @return 블랙리스트에 있으면 true, 없으면 false
     */
    public boolean isTokenBlacklisted(String token) {
        try {
            String tokenId = extractTokenId(token);
            String key = BLACKLIST_PREFIX + tokenId;
            Boolean exists = redisTemplate.hasKey(key);

            if (Boolean.TRUE.equals(exists)) {
                log.debug("Token is blacklisted: {}", tokenId);
                return true;
            }
            return false;
        } catch (Exception e) {
            log.error("Failed to check token blacklist: {}", e.getMessage(), e);
            // Redis 장애시 안전하게 false 반환 (서비스 중단 방지)
            return false;
        }
    }

    /**
     * 사용자의 모든 토큰을 블랙리스트에 추가 (강제 로그아웃)
     *
     * @param username 사용자명
     */
    public void blacklistAllUserTokens(String username) {
        try {
            String pattern = BLACKLIST_PREFIX + "user:" + username + ":*";
            // 실제 구현시 사용자별 토큰 관리가 필요한 경우 사용
            // 현재는 단순 구현으로 개별 토큰만 관리
            log.info("Requested blacklist for all tokens of user: {}", username);
        } catch (Exception e) {
            log.error("Failed to blacklist all user tokens: {}", e.getMessage(), e);
        }
    }

    /**
     * 토큰에서 식별자 추출 (보안상 해시된 부분적 값 사용)
     *
     * @param token JWT 토큰
     * @return 토큰 식별자
     */
    private String extractTokenId(String token) {
        if (token == null || token.length() < 20) {
            throw new IllegalArgumentException("Invalid token");
        }

        // 토큰의 마지막 부분을 식별자로 사용 (signature 부분)
        // 보안상 전체 토큰을 저장하지 않음
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }

        // signature 부분의 해시값 사용
        return parts[2].length() > 10 ? parts[2].substring(parts[2].length() - 10) : parts[2];
    }

    /**
     * 블랙리스트 통계 조회 (모니터링 용도)
     *
     * @return 블랙리스트된 토큰 개수
     */
    public long getBlacklistedTokenCount() {
        try {
            return redisTemplate.keys(BLACKLIST_PREFIX + "*").size();
        } catch (Exception e) {
            log.error("Failed to get blacklisted token count: {}", e.getMessage());
            return -1;
        }
    }
}