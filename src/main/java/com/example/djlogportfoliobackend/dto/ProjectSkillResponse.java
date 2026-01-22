package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.UUID;

/**
 * 프로젝트에서 사용된 기술 스택 정보를 담는 응답 DTO
 */
@Data
public class ProjectSkillResponse {

    /**
     * 기술 스택 고유 식별자
     */
    private UUID id;

    /**
     * 기술 스택 이름 (예: Java, Spring Boot, React 등)
     */
    private String name;

    /**
     * 기술 카테고리 (예: Backend, Frontend, Database 등)
     */
    private String category;
}