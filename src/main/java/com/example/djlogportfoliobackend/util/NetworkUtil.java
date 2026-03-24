package com.example.djlogportfoliobackend.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 네트워크 관련 유틸리티 클래스
 *
 * <p>HTTP 요청 처리 시 필요한 네트워크 관련 공통 기능을 제공합니다.
 * 특히 프록시나 로드밸런서 환경에서의 실제 클라이언트 IP 추출에 중점을 둡니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>다양한 프록시 헤더를 고려한 클라이언트 IP 추출</li>
 *   <li>CDN, 로드밸런서 환경에서의 원본 IP 획득</li>
 *   <li>보안 및 로깅을 위한 신뢰할 수 있는 IP 정보 제공</li>
 * </ul>
 *
 * <p>지원하는 헤더 우선순위:</p>
 * <ol>
 *   <li>X-Forwarded-For (가장 일반적, 쉼표로 구분된 첫 번째 IP 사용)</li>
 *   <li>X-Real-IP (nginx 등에서 사용)</li>
 *   <li>Remote Address (직접 연결 시 사용되는 기본값)</li>
 * </ol>
 *
 * <p>사용 예시:</p>
 * <pre>{@code
 * // Rate limiting에서 사용
 * String clientIp = NetworkUtil.getClientIpAddress(request);
 *
 * // 보안 로그에서 사용
 * log.info("Login attempt from IP: {}", NetworkUtil.getClientIpAddress(request));
 * }</pre>
 *
 * <p>주의사항:</p>
 * <ul>
 *   <li>X-Forwarded-For 헤더는 클라이언트가 조작 가능하므로 신뢰할 수 없는 환경에서는 추가 검증 필요</li>
 *   <li>프록시 설정에 따라 헤더 값이 달라질 수 있음</li>
 *   <li>IPv6 환경에서의 동작 확인 필요</li>
 * </ul>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
public class NetworkUtil {

    /**
     * 클라이언트 실제 IP 주소 추출
     *
     * <p>프록시, 로드밸런서, CDN 등을 통한 요청에서도 실제 클라이언트 IP를 추출합니다.
     * 다양한 HTTP 헤더를 순차적으로 확인하여 신뢰할 수 있는 IP 주소를 반환합니다.</p>
     *
     * <p>검사 순서:</p>
     * <ol>
     *   <li>X-Forwarded-For: 프록시를 거친 경우, 쉼표로 구분된 첫 번째 IP</li>
     *   <li>X-Real-IP: nginx 등에서 설정한 원본 IP</li>
     *   <li>RemoteAddr: 직접 연결된 클라이언트 IP (프록시 없음)</li>
     * </ol>
     *
     * <p>활용 분야:</p>
     * <ul>
     *   <li>Rate Limiting: IP 기반 요청 제한</li>
     *   <li>Security Logging: 보안 이벤트 추적</li>
     *   <li>Analytics: 지역별 접속 통계</li>
     *   <li>Access Control: IP 기반 접근 제어</li>
     * </ul>
     *
     * @param request HTTP 요청 객체 (null 불가)
     * @return 클라이언트 IP 주소 문자열 (IPv4 또는 IPv6 형식)
     * @throws NullPointerException request가 null인 경우
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