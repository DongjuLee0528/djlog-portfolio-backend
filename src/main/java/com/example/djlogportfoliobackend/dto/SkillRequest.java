package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 기술 스택 생성/수정 요청 DTO
 */
@Data
public class SkillRequest {

    /**
     * 기술명 (필수)
     * 예: Java, Spring Boot, React 등
     */
    @NotBlank(message = "기술명은 필수입니다")
    private String name;

    /**
     * 기술 카테고리 (필수)
     * 예: Backend, Frontend, Database, DevOps 등
     */
    @NotBlank(message = "카테고리는 필수입니다")
    private String category;

    /**
     * 숙련도 (선택)
     * 예: Beginner, Intermediate, Advanced 등
     */
    private String proficiency;
}