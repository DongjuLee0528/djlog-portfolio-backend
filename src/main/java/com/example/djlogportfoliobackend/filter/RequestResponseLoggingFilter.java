package com.example.djlogportfoliobackend.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Slf4j
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestId = UUID.randomUUID().toString().substring(0, 8);
        MDC.put("requestId", requestId);

        long startTime = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
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

    private boolean shouldLogBody(HttpServletRequest request) {
        String contentType = request.getContentType();
        return contentType != null &&
               (contentType.contains("application/json") ||
                contentType.contains("application/x-www-form-urlencoded") ||
                contentType.contains("text/"));
    }

    private boolean shouldLogResponseBody(ContentCachingResponseWrapper response) {
        String contentType = response.getContentType();
        return contentType != null &&
               (contentType.contains("application/json") ||
                contentType.contains("text/"));
    }

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