package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EducationRequest {
    @NotBlank(message = "학교명은 필수입니다")
    private String school;

    private String major;

    private String period;

    private String degree;
}