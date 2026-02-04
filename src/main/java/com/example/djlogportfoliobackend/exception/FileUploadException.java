package com.example.djlogportfoliobackend.exception;

/**
 * 파일 업로드 관련 예외 클래스
 *
 * <p>파일 업로드 과정에서 발생하는 모든 예외를 처리하는 커스텀 예외 클래스입니다.
 * 파일 크기, 형식, 보안 검증 등에서 문제가 발생할 때 사용됩니다.</p>
 *
 * <p>주요 발생 상황:
 * <ul>
 *   <li>지원하지 않는 파일 형식 (JPEG, PNG, GIF, WebP 외)</li>
 *   <li>파일 크기 제한 초과 (10MB 초과)</li>
 *   <li>빈 파일 또는 손상된 파일</li>
 *   <li>파일 저장 경로 오류 (Path Traversal 공격 포함)</li>
 *   <li>디스크 용량 부족 또는 I/O 오류</li>
 *   <li>악성 파일 헤더 검증 실패</li>
 * </ul>
 * </p>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
public class FileUploadException extends RuntimeException {

    /**
     * 메시지를 포함한 파일 업로드 예외 생성
     *
     * @param message 파일 업로드 실패 상세 메시지
     */
    public FileUploadException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인을 포함한 파일 업로드 예외 생성
     *
     * @param message 파일 업로드 실패 상세 메시지
     * @param cause 예외의 원인이 되는 Throwable
     */
    public FileUploadException(String message, Throwable cause) {
        super(message, cause);
    }
}