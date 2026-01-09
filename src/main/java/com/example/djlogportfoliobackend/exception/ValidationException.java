package com.example.djlogportfoliobackend.exception;

/**
 * 유효성 검증 예외
 * 입력 데이터 검증 실패 시 발생
 */
public class ValidationException extends RuntimeException {

    public ValidationException(String message) {
        super(message);
    }

    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}