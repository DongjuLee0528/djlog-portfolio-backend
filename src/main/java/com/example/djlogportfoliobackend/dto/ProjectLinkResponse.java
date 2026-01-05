package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProjectLinkResponse {

    private UUID id;
    private UUID projectId;
    private String label;
    private String url;
    private String description;
}