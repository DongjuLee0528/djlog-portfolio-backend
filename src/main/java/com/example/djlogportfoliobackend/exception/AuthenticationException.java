package com.example.djlogportfoliobackend.exception;

/**
 * 인증 관련 예외
 * 잘못된 자격증명, 계정 잠김 등의 경우 발생
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}