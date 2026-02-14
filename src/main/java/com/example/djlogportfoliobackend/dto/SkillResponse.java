package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.Skill;
import lombok.Data;

import java.util.UUID;

@Data
public class SkillResponse {
    private UUID id;
    private String name;
    private String category;
    private String proficiency;

    public SkillResponse(Skill skill) {
        this.id = skill.getId();
        this.name = skill.getName();
        this.category = skill.getCategory();
        this.proficiency = skill.getProficiency();
    }
}