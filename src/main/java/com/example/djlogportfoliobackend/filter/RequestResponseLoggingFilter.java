package com.example.djlogportfoliobackend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

/**
 * 요청/응답 로그 필터
 * HTTP 요청과 응답의 상세 내용을 로그로 기록합니다.
 * 요청과 응답의 바디를 캐시하여 로그로 기록하고 디버그 목적으로 사용합니다.
 */
@Slf4j
@Component
@Order(1)
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    /**
     * 요청/응답 로그 처리
     * ContentCachingWrapper를 사용하여 요청과 응답의 바디를 캐시하고 로그로 기록합니다.
     *
     * @param request HTTP 요청
     * @param response HTTP 응답
     * @param filterChain 필터 체인
     * @throws ServletException 서블릿 예외
     * @throws IOException I/O 예외
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request, 1024);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        try {
            logRequest(requestWrapper, requestId);

            filterChain.doFilter(requestWrapper, responseWrapper);

            long duration = System.currentTimeMillis() - startTime;
            logResponse(responseWrapper, requestId, duration);

        } finally {
            responseWrapper.copyBodyToResponse();
            MDC.clear();
        }
    }

    /**
     * HTTP 요청 로그 기록
     * 요청 메서드, URI, 클라이언트 IP 등의 기본 정보와 요청 바디를 로그로 기록합니다.
     *
     * @param request 캐시된 HTTP 요청
     * @param requestId 요청 식별자
     */
    private void logRequest(ContentCachingRequestWrapper request, String requestId) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIp = getClientIP(request);

        log.info("[REQ] {} {} {} | IP: {} | RequestId: {}",
                method, uri, queryString != null ? "?" + queryString : "", clientIp, requestId);

        if (shouldLogBody(request)) {
            byte[] content = request.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                log.debug("[REQ-BODY] RequestId: {} | Body: {}", requestId, body);
            }
        }
    }

    /**
     * HTTP 응답 로그 기록
     * 응답 상태 코드, 처리 시간, 응답 바디를 로그로 기록합니다.
     *
     * @param response 캐시된 HTTP 응답
     * @param requestId 요청 식별자
     * @param duration 요청 처리 시간(밀리초)
     */
    private void logResponse(ContentCachingResponseWrapper response, String requestId, long duration) {
        int status = response.getStatus();

        log.info("[RES] Status: {} | Duration: {}ms | RequestId: {}", status, duration, requestId);

        if (shouldLogResponseBody(response)) {
            byte[] content = response.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                log.debug("[RES-BODY] RequestId: {} | Body: {}", requestId, body);
            }
        }
    }

    /**
     * 요청 바디 로그 여부 판단
     * Content-Type을 기반으로 로그 기록 여부를 결정합니다.
     *
     * @param request HTTP 요청
     * @return 로그 기록 여부
     */
    private boolean shouldLogBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null &&
               (contentType.contains("application/json") ||
                contentType.contains("application/x-www-form-urlencoded") ||
                contentType.contains("text/"));
    }

    /**
     * 응답 바디 로그 여부 판단
     * Content-Type을 기반으로 로그 기록 여부를 결정합니다.
     *
     * @param response 캐시된 HTTP 응답
     * @return 로그 기록 여부
     */
    private boolean shouldLogResponseBody(ContentCachingResponseWrapper response) {
        String contentType = response.getContentType();
        return contentType != null &&
               (contentType.contains("application/json") ||
                contentType.contains("text/"));
    }

    /**
     * 클라이언트 IP 주소 추출
     * 프록시나 로드밸런서 환경에서의 실제 IP 주소를 가져옵니다.
     *
     * @param request HTTP 요청
     * @return 클라이언트 IP 주소
     */
    private String getClientIP(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIP = request.getHeader("X-Real-IP");
        if (xRealIP != null && !xRealIP.isEmpty()) {
            return xRealIP;
        }

        return request.getRemoteAddr();
    }

    /**
     * 필터 제외 경로 판단
     * 특정 경로(정적 리소스, 관리 콘솔 등)에 대해서는 필터를 적용하지 않습니다.
     *
     * @param request HTTP 요청
     * @return 필터 제외 여부
     */
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/h2-console") ||
               path.startsWith("/actuator") ||
               path.endsWith(".css") ||
               path.endsWith(".js") ||
               path.endsWith(".png") ||
               path.endsWith(".jpg") ||
               path.endsWith(".ico");
    }
}