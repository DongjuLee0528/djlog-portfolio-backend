package com.example.djlogportfoliobackend.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.UUID;

/**
 * 로그 설정
 * HTTP 요청 로그를 위한 Spring의 CommonsRequestLoggingFilter를 구성합니다.
 * MDC(Mapped Diagnostic Context)를 사용하여 TraceID 및 요청 정보를 관리합니다.
 */
@Configuration
public class LoggingConfig {

    /**
     * 요청 로그 필터 빈 생성
     * Spring의 CommonsRequestLoggingFilter를 확장하여 TraceID 및 요청 정보를
     * MDC에 저장하는 커스텀 로그 필터를 생성합니다.
     *
     * 필터 설정:
     * - 쿼리 스트링, 페이로드, 클라이언트 정보 포함
     * - 최대 페이로드 길이: 1000바이트
     * - 헤더 정보는 제외
     *
     * @return 구성된 CommonsRequestLoggingFilter
     */
    @Bean
    public CommonsRequestLoggingFilter requestLoggingFilter() {
        CommonsRequestLoggingFilter filter = new CommonsRequestLoggingFilter() {
            @Override
            protected void beforeRequest(jakarta.servlet.http.HttpServletRequest request, String message) {
                MDC.put("traceId", UUID.randomUUID().toString());
                MDC.put("method", request.getMethod());
                MDC.put("uri", request.getRequestURI());
                MDC.put("queryString", request.getQueryString());
                MDC.put("remoteAddr", request.getRemoteAddr());
                MDC.put("userAgent", request.getHeader("User-Agent"));
                super.beforeRequest(request, message);
            }

            @Override
            protected void afterRequest(jakarta.servlet.http.HttpServletRequest request, String message) {
                super.afterRequest(request, message);
                MDC.clear();
            }
        };

        filter.setIncludeQueryString(true);
        filter.setIncludePayload(true);
        filter.setIncludeHeaders(false);
        filter.setIncludeClientInfo(true);
        filter.setMaxPayloadLength(1000);
        filter.setAfterMessagePrefix("REQUEST DATA : ");

        return filter;
    }
}