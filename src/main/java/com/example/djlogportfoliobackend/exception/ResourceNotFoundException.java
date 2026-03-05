package com.example.djlogportfoliobackend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * 요청된 리소스를 찾을 수 없을 때 발생하는 커스텀 예외 클래스
 * HTTP 404 Not Found 상태 코드로 자동 매핑됩니다.
 *
 * @author DongJu Lee
 * @version 1.0
 * @since 1.0
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    /**
     * 메시지를 포함하는 ResourceNotFoundException을 생성합니다.
     *
     * @param message 예외 상세 메시지
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인을 포함하는 ResourceNotFoundException을 생성합니다.
     *
     * @param message 예외 상세 메시지
     * @param cause 예외의 원인
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}