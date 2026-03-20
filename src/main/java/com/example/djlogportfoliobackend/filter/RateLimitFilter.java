package com.example.djlogportfoliobackend.filter;

import com.example.djlogportfoliobackend.util.NetworkUtil;
import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 요청 빈도 제한 필터 (Rate Limiting)
 * IP+URI 조합별로 분당 요청 수를 제한하여 API 남용을 방지합니다.
 * Caffeine 캐시와 AtomicInteger를 사용하여 thread-safe하게 카운터를 관리합니다.
 *
 * 개선 사항:
 * - IP+URI 기준으로 세밀한 제어
 * - AtomicInteger를 통한 thread-safe한 카운트 연산
 * - TraceId 자동 생성으로 요청 추적 가능
 * - 더 상세한 로깅 정보 제공
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final Cache<String, AtomicInteger> rateLimitCache;

    @Value("${security.rate-limit.requests-per-minute:100}")
    private int maxRequestsPerMinute;

    @Value("${security.rate-limit.strategy:ip_uri}")
    private String rateLimitStrategy;

    @Value("${security.rate-limit.enable-global-limit:false}")
    private boolean enableGlobalLimit;

    @Value("${security.rate-limit.global-requests-per-minute:1000}")
    private int maxGlobalRequestsPerMinute;

    /**
     * 요청 빈도 제한 처리
     * 설정된 전략(IP, IP+URI, 글로벌)에 따라 요청 수를 검증하고 한계를 초과하면 429 에러를 반환합니다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException I/O 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // TraceId 생성 또는 기존 것 사용
        String traceId = MDC.get("traceId");
        if (traceId == null || traceId.trim().isEmpty()) {
            traceId = UUID.randomUUID().toString().substring(0, 8);
            MDC.put("traceId", traceId);
        }

        String clientIp = NetworkUtil.getClientIpAddress(request);
        String requestUri = request.getRequestURI();
        String method = request.getMethod();
        String requestInfo = String.format("%s %s", method, requestUri);

        // Rate limit 키 생성 전략
        String rateLimitKey = generateRateLimitKey(clientIp, requestUri, rateLimitStrategy);

        log.debug("[RATE_LIMIT] Processing request: {} from IP: {} - TraceId: {} - Key: {}",
                requestInfo, clientIp, traceId, rateLimitKey);

        // Atomic한 카운트 증가 및 체크
        AtomicInteger counter = rateLimitCache.get(rateLimitKey, k -> new AtomicInteger(0));
        int currentCount = counter.incrementAndGet();

        log.debug("[RATE_LIMIT] Updated count for key {}: {} (max: {}) - TraceId: {}",
                rateLimitKey, currentCount, maxRequestsPerMinute, traceId);

        if (currentCount > maxRequestsPerMinute) {
            log.warn("[RATE_LIMIT] Rate limit exceeded - TraceId: {} - IP: {} - Count: {} - Request: {} - Strategy: {} - Key: {}",
                    traceId, clientIp, currentCount, requestInfo, rateLimitStrategy, rateLimitKey);

            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                "{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests. Please try again later.\",\"traceId\":\"%s\",\"strategy\":\"%s\"}",
                traceId, rateLimitStrategy));
            return;
        }

        // 글로벌 제한도 체크 (설정된 경우)
        if (enableGlobalLimit && !checkGlobalLimit(traceId)) {
            response.setStatus(429);
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                "{\"error\":\"Global rate limit exceeded\",\"message\":\"Server overloaded. Please try again later.\",\"traceId\":\"%s\"}",
                traceId));
            return;
        }

        log.debug("[RATE_LIMIT] Allowing request: {} from IP: {} - TraceId: {}", requestInfo, clientIp, traceId);
        filterChain.doFilter(request, response);
        log.debug("[RATE_LIMIT] Completed request: {} from IP: {} - TraceId: {}", requestInfo, clientIp, traceId);
    }

    /**
     * Rate limit 키 생성 전략
     *
     * @param clientIp 클라이언트 IP
     * @param requestUri 요청 URI
     * @param strategy 키 생성 전략 (ip, ip_uri, global)
     * @return 생성된 키
     */
    private String generateRateLimitKey(String clientIp, String requestUri, String strategy) {
        return switch (strategy.toLowerCase()) {
            case "ip" -> "rate_limit:ip:" + clientIp;
            case "ip_uri" -> "rate_limit:ip_uri:" + clientIp + ":" + requestUri;
            case "global" -> "rate_limit:global:all";
            default -> "rate_limit:ip_uri:" + clientIp + ":" + requestUri; // 기본값
        };
    }

    /**
     * 글로벌 요청 제한 체크
     *
     * @param traceId 추적 ID
     * @return 제한 통과 여부
     */
    private boolean checkGlobalLimit(String traceId) {
        String globalKey = "rate_limit:global:all";
        AtomicInteger globalCounter = rateLimitCache.get(globalKey, k -> new AtomicInteger(0));
        int globalCount = globalCounter.incrementAndGet();

        if (globalCount > maxGlobalRequestsPerMinute) {
            log.warn("[RATE_LIMIT] Global rate limit exceeded - TraceId: {} - Global count: {} (max: {})",
                    traceId, globalCount, maxGlobalRequestsPerMinute);
            return false;
        }

        log.debug("[RATE_LIMIT] Global count: {} (max: {}) - TraceId: {}",
                globalCount, maxGlobalRequestsPerMinute, traceId);
        return true;
    }

}