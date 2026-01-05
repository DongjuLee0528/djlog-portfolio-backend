package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectQnAResponse {

    private UUID id;
    private UUID projectId;
    private String question;
    private String answer;
}