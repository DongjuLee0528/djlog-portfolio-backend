package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 학력 정보 등록/수정 요청 DTO
 * 클라이언트로부터 학력 정보를 받기 위한 데이터 전송 객체
 */
@Data
public class EducationRequest {
    @NotBlank(message = "학교명은 필수입니다")
    private String school;

    private String major;

    private String period;

    private String degree;
}