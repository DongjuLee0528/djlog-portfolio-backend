package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class EducationRequest {
    @NotBlank(message = "학교명은 필수입니다")
    private String school;

    private String major;

    @Pattern(regexp = "^\\d{4}\\.\\d{2} - (\\d{4}\\.\\d{2}|현재)$", message = "기간은 'YYYY.MM - YYYY.MM' 또는 'YYYY.MM - 현재' 형식이어야 합니다")
    private String period;

    private String degree;
}