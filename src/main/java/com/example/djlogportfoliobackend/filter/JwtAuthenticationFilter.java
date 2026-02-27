package com.example.djlogportfoliobackend.filter;

import com.example.djlogportfoliobackend.util.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 인증 필터
 * HTTP 요청에서 JWT 토큰을 추출하고 유효성을 검증하여 Spring Security 컨텍스트에 인증 정보를 설정합니다.
 * Authorization 헤더의 Bearer 토큰을 처리합니다.
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    /**
     * JWT 인증 필터 생성자
     *
     * @param jwtUtil JWT 유틸리티 클래스
     */
    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    /**
     * JWT 토큰 인증 처리
     * Authorization 헤더에서 JWT 토큰을 추출하고 유효성을 검증한 후,
     * 유효한 경우 Spring Security 컨텍스트에 인증 정보를 설정합니다.
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

        final String authorizationHeader = request.getHeader("Authorization");

        String username = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                logger.debug("JWT token expired: " + e.getMessage());
            } catch (MalformedJwtException e) {
                logger.debug("JWT token malformed: " + e.getMessage());
            } catch (UnsupportedJwtException e) {
                logger.debug("JWT token unsupported: " + e.getMessage());
            } catch (SignatureException e) {
                logger.debug("JWT signature invalid: " + e.getMessage());
            } catch (IllegalArgumentException e) {
                logger.debug("JWT token argument invalid: " + e.getMessage());
            } catch (Exception e) {
                logger.warn("Unexpected JWT token processing error: " + e.getMessage());
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            if (jwtUtil.validateToken(jwt, username)) {
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}