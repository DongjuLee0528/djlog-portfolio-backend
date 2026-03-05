package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

/**
 * 학력 정보 등록/수정 요청을 위한 DTO 클래스
 * 클라이언트로부터 학력 정보를 받아 서비스로 전달하는 역할을 수행합니다.
 *
 * @author DongJu Lee
 * @version 1.0
 * @since 1.0
 */
@Data
public class EducationRequest {

    /**
     * 학교명 또는 교육기관명
     * 예: 서울대학교, 한국대학교 등
     */
    @NotBlank(message = "학교명은 필수입니다")
    private String school;

    /**
     * 전공명
     * 예: 컴퓨터공학과, 경영학과 등 (선택사항)
     */
    private String major;

    /**
     * 교육 기간
     * 형식: YYYY.MM - YYYY.MM 또는 YYYY.MM - 현재
     * 예: 2018.03 - 2022.02, 2020.09 - 현재
     */
    @Pattern(regexp = "^\\d{4}\\.\\d{2} - (\\d{4}\\.\\d{2}|현재)$", message = "기간은 'YYYY.MM - YYYY.MM' 또는 'YYYY.MM - 현재' 형식이어야 합니다")
    private String period;

    /**
     * 학위 또는 과정명
     * 예: 학사, 석사, 박사, 수료증 등 (선택사항)
     */
    private String degree;
}