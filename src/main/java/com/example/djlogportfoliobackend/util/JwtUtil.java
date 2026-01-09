package com.example.djlogportfoliobackend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Slf4j
@Component
public class JwtUtil {

    private final SecretKey key;
    private final long jwtExpiration;
    private final StringRedisTemplate redisTemplate;
    private static final String BLACKLIST_PREFIX = "jwt:blacklist:";

    public JwtUtil(@Value("${jwt.secret}") String secret,
                   @Value("${jwt.expiration:86400000}") long jwtExpiration,
                   StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;

        // JWT Secret 검증
        if (secret == null || secret.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT secret must be configured");
        }
        if (secret.getBytes().length < 32) {
            throw new IllegalArgumentException("JWT secret must be at least 32 bytes long");
        }
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpiration = jwtExpiration;
    }

    public String generateToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);

        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, ClaimsResolver<T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.resolve(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * JWT 토큰 유효성 검증 (블랙리스트 포함)
     *
     * @param token JWT 토큰
     * @param username 검증할 사용자명
     * @return 유효한 토큰이면 true, 무효하면 false
     */
    public boolean validateToken(String token, String username) {
        try {
            // 1. 블랙리스트 확인
            if (isTokenBlacklisted(token)) {
                log.debug("Token is blacklisted: {}", token.substring(0, Math.min(20, token.length())));
                return false;
            }

            // 2. 기본 토큰 검증
            final String extractedUsername = extractUsername(token);
            boolean isValid = extractedUsername.equals(username) && !isTokenExpired(token);

            if (!isValid) {
                log.debug("Token validation failed: username={}, extracted={}, expired={}",
                         username, extractedUsername, isTokenExpired(token));
            }

            return isValid;
        } catch (Exception e) {
            log.error("Token validation error: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰을 블랙리스트에 추가 (로그아웃 시 사용)
     *
     * @param token 블랙리스트에 추가할 토큰
     */
    public void invalidateToken(String token) {
        try {
            Date expiration = extractExpiration(token);
            blacklistToken(token, expiration);
            log.info("Token invalidated successfully");
        } catch (Exception e) {
            log.error("Failed to invalidate token: {}", e.getMessage(), e);
        }
    }

    /**
     * JWT 토큰을 블랙리스트에 추가
     *
     * @param token JWT 토큰
     * @param expiration 토큰 만료 시간
     */
    private void blacklistToken(String token, Date expiration) {
        try {
            String tokenId = extractTokenIdFromSignature(token);
            String key = BLACKLIST_PREFIX + tokenId;

            long ttl = Math.max(0, expiration.getTime() - System.currentTimeMillis());
            if (ttl > 0) {
                redisTemplate.opsForValue().set(key, "blacklisted", Duration.ofMillis(ttl));
                log.debug("Token blacklisted: {} (TTL: {}ms)", tokenId, ttl);
            }
        } catch (Exception e) {
            log.error("Failed to blacklist token: {}", e.getMessage());
        }
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인
     *
     * @param token JWT 토큰
     * @return 블랙리스트에 있으면 true
     */
    private boolean isTokenBlacklisted(String token) {
        try {
            String tokenId = extractTokenIdFromSignature(token);
            String key = BLACKLIST_PREFIX + tokenId;
            return Boolean.TRUE.equals(redisTemplate.hasKey(key));
        } catch (Exception e) {
            log.error("Failed to check token blacklist: {}", e.getMessage());
            return false;
        }
    }

    /**
     * 토큰 서명에서 식별자 추출
     *
     * @param token JWT 토큰
     * @return 토큰 식별자
     */
    private String extractTokenIdFromSignature(String token) {
        String[] parts = token.split("\\.");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid JWT token format");
        }
        return parts[2].length() > 10 ? parts[2].substring(parts[2].length() - 10) : parts[2];
    }

    /**
     * 토큰에서 JTI(JWT ID) 추출
     *
     * @param token JWT 토큰
     * @return JWT ID
     */
    public String extractTokenId(String token) {
        return extractClaim(token, claims -> claims.getId());
    }

    /**
     * JTI를 포함한 JWT 토큰 생성
     *
     * @param username 사용자명
     * @return JWT 토큰
     */
    public String generateTokenWithId(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        String tokenId = java.util.UUID.randomUUID().toString();

        return Jwts.builder()
                .subject(username)
                .id(tokenId)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }

    /**
     * 토큰의 만료 시간까지 남은 시간 (밀리초)
     *
     * @param token JWT 토큰
     * @return 남은 시간 (밀리초)
     */
    public long getTimeToExpiration(String token) {
        Date expiration = extractExpiration(token);
        return Math.max(0, expiration.getTime() - System.currentTimeMillis());
    }

    @FunctionalInterface
    public interface ClaimsResolver<T> {
        T resolve(Claims claims);
    }
}