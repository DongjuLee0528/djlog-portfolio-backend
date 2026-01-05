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

@Slf4j
@Component
public class CustomRequestLoggingFilter extends OncePerRequestFilter {

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