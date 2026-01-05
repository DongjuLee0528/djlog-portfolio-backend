package com.example.djlogportfoliobackend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

/**
 * 커스텀 HTTP 요청 로그 필터
 * 모든 HTTP 요청에 대한 로깅을 처리하고 TraceID를 통한 요청 추적을 제공합니다.
 * 요청의 시작부터 완료까지의 전체 사이클을 모니터링합니다.
 */
@Slf4j
@Component
public class CustomRequestLoggingFilter extends OncePerRequestFilter {

    /**
     * HTTP 요청에 대한 로그 처리
     * TraceID 생성, 요청 정보 로그, 응답 시간 측정 등을 수행합니다.
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

        String traceId = UUID.randomUUID().toString();
        long startTime = System.currentTimeMillis();

        try {
            MDC.put("traceId", traceId);
            MDC.put("method", request.getMethod());
            MDC.put("uri", request.getRequestURI());
            MDC.put("queryString", request.getQueryString());
            MDC.put("remoteAddr", getClientIpAddress(request));
            MDC.put("userAgent", request.getHeader("User-Agent"));

            log.info("HTTP Request Started - {} {}", request.getMethod(), request.getRequestURI());

            filterChain.doFilter(request, response);

            long duration = System.currentTimeMillis() - startTime;
            int statusCode = response.getStatus();

            MDC.put("statusCode", String.valueOf(statusCode));
            MDC.put("responseTime", String.valueOf(duration));

            log.info("HTTP Request Completed - {} {} - Status: {} - Duration: {}ms",
                    request.getMethod(), request.getRequestURI(), statusCode, duration);

        } catch (Exception e) {
            long duration = System.currentTimeMillis() - startTime;
            MDC.put("responseTime", String.valueOf(duration));
            log.error("HTTP Request Failed - {} {} - Duration: {}ms - Error: {}",
                    request.getMethod(), request.getRequestURI(), duration, e.getMessage(), e);
            throw e;
        } finally {
            MDC.clear();
        }
    }

    /**
     * 클라이언트 IP 주소 추출
     * 프록시나 로드밸런서를 고려하여 실제 클라이언트 IP를 가져옵니다.
     * X-Forwarded-For > X-Real-IP > Remote Address 순으로 확인합니다.
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