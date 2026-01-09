package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

/**
 * 프로젝트 Q&A 요청 DTO
 * 프로젝트에 대한 질문과 답변을 생성/수정할 때 사용하는 데이터 전송 객체
 */
@Data
public class ProjectQnARequest {

    /**
     * 프로젝트 ID
     * Q&A가 속할 프로젝트의 고유 식별자
     */
    private UUID projectId;

    /**
     * 질문 내용
     * 프로젝트에 대한 질문 텍스트 (필수)
     */
    @NotBlank(message = "질문은 필수입니다")
    private String question;

    /**
     * 답변 내용
     * 질문에 대한 답변 텍스트 (필수)
     */
    @NotBlank(message = "답변은 필수입니다")
    private String answer;
}