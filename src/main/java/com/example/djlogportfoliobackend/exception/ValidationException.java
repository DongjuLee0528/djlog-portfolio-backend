package com.example.djlogportfoliobackend.exception;

/**
 * 데이터 유효성 검증 예외 클래스
 *
 * <p>비즈니스 로직 수행 중 입력 데이터의 유효성 검증에 실패했을 때 발생하는 커스텀 예외입니다.
 * Spring의 @Valid 어노테이션과는 별도로, 복잡한 비즈니스 룰 검증에서 사용됩니다.</p>
 *
 * <p>주요 발생 상황:
 * <ul>
 *   <li>비즈니스 룰 위반 (예: 중복 데이터, 참조 무결성)</li>
 *   <li>데이터 조건 불일치 (예: 날짜 범위, 숫자 범위)</li>
 *   <li>상태 기반 검증 실패 (예: 이미 삭제된 엔티티 수정)</li>
 *   <li>외부 시스템 연동 시 데이터 불일치</li>
 * </ul>
 * </p>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
public class ValidationException extends RuntimeException {

    /**
     * 메시지를 포함한 유효성 검증 예외 생성
     *
     * @param message 검증 실패 상세 메시지
     */
    public ValidationException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인을 포함한 유효성 검증 예외 생성
     *
     * @param message 검증 실패 상세 메시지
     * @param cause 예외의 원인이 되는 Throwable
     */
    public ValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}