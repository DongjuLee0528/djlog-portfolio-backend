package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.UUID;

/**
 * 프로젝트 Q&A 정보 응답을 위한 DTO
 * 클라이언트에게 프로젝트 Q&A 정보를 전송하기 위한 데이터 전송 객체
 */
@Data
public class ProjectQnAResponse {

    /** Q&A의 고유 식별자 */
    private UUID id;

    /** Q&A가 속한 프로젝트의 ID */
    private UUID projectId;

    /** 질문 내용 */
    private String question;

    /** 질문에 대한 답변 내용 */
    private String answer;
}