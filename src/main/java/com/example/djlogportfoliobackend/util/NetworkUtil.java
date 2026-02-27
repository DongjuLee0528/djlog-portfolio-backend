package com.example.djlogportfoliobackend.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 네트워크 관련 유틸리티 클래스
 * 클라이언트 IP 주소 추출 등의 공통 네트워크 기능을 제공합니다.
 */
public class NetworkUtil {

    /**
     * 클라이언트 IP 주소 추출
     * 프록시나 로드 밸런서를 통한 요청에서도 실제 클라이언트 IP를 추출합니다.
     *
     * @param request HTTP 요청 객체
     * @return 클라이언트 IP 주소
     */
    public static String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty() && !"unknown".equalsIgnoreCase(xForwardedFor)) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty() && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}