package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.ProjectStatus;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class ProjectResponse {

    private UUID id;
    private String title;
    private String category;
    private ProjectStatus status;
    private String description;
    private String image;
    private List<String> tags;
    private Integer order;
}