package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.UUID;

/**
 * 기술 정보 응답 DTO 클래스
 * 클라이언트에게 기술 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
public class SkillResponse {
    /**
     * 기술 고유 식별자
     */
    private UUID id;

    /**
     * 기술 이름
     */
    private String name;

    /**
     * 기술 카테고리 (예: Frontend, Backend, Database)
     */
    private String category;

    /**
     * 기술 숙련도 (예: Beginner, Intermediate, Advanced)
     */
    private String proficiency;
}