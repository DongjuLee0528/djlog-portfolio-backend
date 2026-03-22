package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.Achievement;
import lombok.Data;

import java.util.UUID;

/**
 * 성과/경험 응답 DTO입니다.
 */
@Data
public class AchievementResponse {
    private UUID id;
    private String title;
    private String organization;
    private String description;
    private String period;
    private String category;

    public AchievementResponse(Achievement achievement) {
        this.id = achievement.getId();
        this.title = achievement.getTitle();
        this.organization = achievement.getOrganization();
        this.description = achievement.getDescription();
        this.period = achievement.getPeriod();
        this.category = achievement.getCategory();
    }
}
