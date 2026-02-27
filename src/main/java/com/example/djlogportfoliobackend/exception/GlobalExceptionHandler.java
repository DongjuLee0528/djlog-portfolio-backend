package com.example.djlogportfoliobackend.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 전역 예외 처리 핸들러
 * 애플리케이션 전체에서 발생하는 예외를 일관되게 처리합니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @Value("${logging.level.com.example.djlogportfoliobackend:INFO}")
    private String loggingLevel;

    /**
     * 리소스 찾을 수 없음 예외 처리 (404)
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleResourceNotFoundException(ResourceNotFoundException e) {
        log.warn("Resource not found: {}", e.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "RESOURCE_NOT_FOUND");
        error.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    /**
     * 인증 예외 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthenticationException(AuthenticationException e) {
        log.warn("Authentication failed: {}", e.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "AUTHENTICATION_FAILED");
        error.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * 유효성 검증 예외 처리
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException e) {
        log.warn("Validation failed: {}", e.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "VALIDATION_FAILED");
        error.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * Spring Validation 예외 처리 (@Valid 어노테이션)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.warn("Validation failed for request: {}", e.getMessage());

        Map<String, Object> error = new HashMap<>();
        Map<String, String> fieldErrors = new HashMap<>();

        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
        }

        error.put("error", "VALIDATION_FAILED");
        error.put("message", "입력 데이터 검증에 실패했습니다.");
        error.put("fieldErrors", fieldErrors);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * 파일 업로드 예외 처리
     */
    @ExceptionHandler(FileUploadException.class)
    public ResponseEntity<Map<String, String>> handleFileUploadException(FileUploadException e) {
        log.warn("File upload error: {}", e.getMessage());

        Map<String, String> error = new HashMap<>();
        error.put("error", "FILE_UPLOAD_ERROR");
        error.put("message", e.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    /**
     * 일반 런타임 예외 처리
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException e) {
        if (isDebugLoggingEnabled()) {
            log.error("Unexpected runtime exception: {}", e.getMessage(), e);
        } else {
            log.error("Unexpected runtime exception: {}", e.getMessage());
        }

        Map<String, String> error = new HashMap<>();
        error.put("error", "INTERNAL_ERROR");
        error.put("message", "서버 내부 오류가 발생했습니다.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * 예상치 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e) {
        if (isDebugLoggingEnabled()) {
            log.error("Unexpected exception: {}", e.getMessage(), e);
        } else {
            log.error("Unexpected exception: {}", e.getMessage());
        }

        Map<String, String> error = new HashMap<>();
        error.put("error", "UNKNOWN_ERROR");
        error.put("message", "예상치 못한 오류가 발생했습니다.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    /**
     * 디버그 로깅 레벨 확인
     * DEBUG 레벨일 때만 스택 트레이스를 로그에 출력
     */
    private boolean isDebugLoggingEnabled() {
        return "DEBUG".equalsIgnoreCase(loggingLevel) || "TRACE".equalsIgnoreCase(loggingLevel);
    }
}