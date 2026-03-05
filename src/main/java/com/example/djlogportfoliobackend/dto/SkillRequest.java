package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 기술 스택 등록/수정 요청을 위한 DTO 클래스
 * 클라이언트로부터 기술 정보를 받아 서비스로 전달하는 역할을 수행합니다.
 *
 * @author DongJu Lee
 * @version 1.0
 * @since 1.0
 */
@Data
public class SkillRequest {

    /**
     * 기술명
     * 예: Java, Spring Boot, React 등
     */
    @NotBlank(message = "기술명은 필수입니다")
    private String name;

    /**
     * 기술 카테고리
     * 예: Backend, Frontend, Database 등
     */
    @NotBlank(message = "카테고리는 필수입니다")
    private String category;

    /**
     * 기술 숙련도 수준
     * Beginner(초급), Intermediate(중급), Advanced(고급), Expert(전문가) 중 선택
     */
    @Pattern(regexp = "^(Beginner|Intermediate|Advanced|Expert)$", message = "숙련도는 Beginner, Intermediate, Advanced, Expert 중 하나여야 합니다")
    private String proficiency;
}