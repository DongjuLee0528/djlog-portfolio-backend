package com.example.djlogportfoliobackend.exception;

/**
 * 인증 관련 예외 클래스
 *
 * <p>사용자 인증 과정에서 발생하는 모든 예외를 처리하는 커스텀 예외 클래스입니다.
 * 잘못된 로그인 정보, 토큰 만료, 권한 부족 등의 상황에서 발생합니다.</p>
 *
 * <p>주요 발생 상황:
 * <ul>
 *   <li>잘못된 이메일 또는 비밀번호</li>
 *   <li>만료된 JWT 토큰</li>
 *   <li>블랙리스트에 등록된 토큰</li>
 *   <li>계정 잠금 또는 비활성화</li>
 * </ul>
 * </p>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
public class AuthenticationException extends RuntimeException {

    /**
     * 메시지를 포함한 인증 예외 생성
     *
     * @param message 예외 상세 메시지
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인을 포함한 인증 예외 생성
     *
     * @param message 예외 상세 메시지
     * @param cause 예외의 원인이 되는 Throwable
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}