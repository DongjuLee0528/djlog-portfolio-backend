package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 기술 스택 정보 등록/수정 요청 DTO
 * 클라이언트로부터 기술 스택 정보를 받기 위한 데이터 전송 객체
 */
@Data
public class SkillRequest {
    @NotBlank(message = "기술명은 필수입니다")
    private String name;

    @NotBlank(message = "카테고리는 필수입니다")
    private String category;

    private String proficiency;
}