package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 프로젝트에 사용될 기술 스택 요청 DTO
 */
@Data
public class ProjectSkillRequest {

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
}