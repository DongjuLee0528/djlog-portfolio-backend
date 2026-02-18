package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class SkillRequest {
    @NotBlank(message = "기술명은 필수입니다")
    private String name;

    @NotBlank(message = "카테고리는 필수입니다")
    private String category;

    @Pattern(regexp = "^(Beginner|Intermediate|Advanced|Expert)$", message = "숙련도는 Beginner, Intermediate, Advanced, Expert 중 하나여야 합니다")
    private String proficiency;
}