package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 성과/경험 등록 및 수정 요청 DTO입니다.
 */
@Data
public class AchievementRequest {

    @NotBlank(message = "성과 제목은 필수입니다")
    private String title;

    private String organization;

    private String description;

    private String period;

    private String category;
}
