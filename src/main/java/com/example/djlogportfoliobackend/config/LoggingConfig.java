package com.example.djlogportfoliobackend.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.CommonsRequestLoggingFilter;

import java.util.UUID;

@Configuration
public class LoggingConfig {

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