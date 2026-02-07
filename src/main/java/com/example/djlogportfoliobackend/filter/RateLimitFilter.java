package com.example.djlogportfoliobackend.filter;

import com.github.benmanes.caffeine.cache.Cache;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 요청 빈도 제한 필터 (Rate Limiting)
 * IP 주소별로 분당 요청 수를 제한하여 API 남용을 방지합니다.
 * Caffeine 캐시를 사용하여 메모리 기반으로 카운터를 관리합니다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final Cache<String, Integer> rateLimitCache;
    /** 분당 최대 요청 수 */
    private static final int MAX_REQUESTS_PER_MINUTE = 100;

    /**
     * 요청 빈도 제한 처리
     * IP별 요청 수를 검증하고 한계를 초과하면 429 에러를 반환합니다.
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

        String clientIp = getClientIpAddress(request);
        String key = "rate_limit:" + clientIp;
        String requestInfo = String.format("%s %s", request.getMethod(), request.getRequestURI());

        log.debug("[RATE_LIMIT] Processing request: {} from IP: {}", requestInfo, clientIp);

        Integer currentCount = rateLimitCache.getIfPresent(key);
        if (currentCount == null) {
            currentCount = 0;
            log.debug("[RATE_LIMIT] New IP detected: {} - initializing count to 0", clientIp);
        }

        log.debug("[RATE_LIMIT] Current count for IP {}: {} (max: {})", clientIp, currentCount, MAX_REQUESTS_PER_MINUTE);

        if (currentCount >= MAX_REQUESTS_PER_MINUTE) {
            String traceId = MDC.get("traceId");
            log.warn("[RATE_LIMIT] Rate limit exceeded - TraceId: {} - IP: {} - Count: {} - Request: {}",
                    traceId, clientIp, currentCount, requestInfo);

            response.setStatus(429); // 429 Too Many Requests
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Rate limit exceeded\",\"message\":\"Too many requests. Please try again later.\"}");
            return;
        }

        rateLimitCache.put(key, currentCount + 1);
        log.debug("[RATE_LIMIT] Updated count for IP {}: {} -> {}", clientIp, currentCount, currentCount + 1);

        log.debug("[RATE_LIMIT] Allowing request: {} from IP: {}", requestInfo, clientIp);
        filterChain.doFilter(request, response);
        log.debug("[RATE_LIMIT] Completed request: {} from IP: {}", requestInfo, clientIp);
    }

    /**
     * 클라이언트 IP 주소 추출
     * 프록시나 로드밸런서를 고려하여 실제 클라이언트 IP를 가져옵니다.
     *
     * @param request HTTP 요청
     * @return 클라이언트 IP 주소
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}