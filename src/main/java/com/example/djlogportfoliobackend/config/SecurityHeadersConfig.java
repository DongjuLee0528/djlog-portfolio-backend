package com.example.djlogportfoliobackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Configuration
public class SecurityHeadersConfig {

    @Bean
    public OncePerRequestFilter securityHeadersFilter() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain filterChain) throws ServletException, IOException {

                // X-Content-Type-Options
                response.setHeader("X-Content-Type-Options", "nosniff");

                // X-Frame-Options
                response.setHeader("X-Frame-Options", "DENY");

                // X-XSS-Protection
                response.setHeader("X-XSS-Protection", "1; mode=block");

                // Referrer-Policy
                response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");

                // Content-Security-Policy
                response.setHeader("Content-Security-Policy",
                    "default-src 'self'; " +
                    "script-src 'self' 'unsafe-inline' 'unsafe-eval'; " +
                    "style-src 'self' 'unsafe-inline'; " +
                    "img-src 'self' data: https:; " +
                    "font-src 'self' data:; " +
                    "connect-src 'self'; " +
                    "frame-ancestors 'none'");

                // Strict-Transport-Security (HSTS) - only for HTTPS
                if (request.isSecure()) {
                    response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
                }

                // Permissions-Policy
                response.setHeader("Permissions-Policy",
                    "camera=(), " +
                    "microphone=(), " +
                    "geolocation=(), " +
                    "payment=(), " +
                    "usb=(), " +
                    "magnetometer=(), " +
                    "gyroscope=(), " +
                    "accelerometer=()");

                filterChain.doFilter(request, response);
            }
        };
    }
}