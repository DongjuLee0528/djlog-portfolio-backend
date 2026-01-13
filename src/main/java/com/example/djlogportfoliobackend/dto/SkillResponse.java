package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class SkillResponse {
    private UUID id;
    private String name;
    private String category;
    private String proficiency;
}