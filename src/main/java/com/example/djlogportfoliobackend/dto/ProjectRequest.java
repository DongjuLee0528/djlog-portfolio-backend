package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.ProjectStatus;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

@Data
public class ProjectRequest {

    @NotBlank(message = "프로젝트 제목은 필수입니다")
    private String title;

    private String category;

    private ProjectStatus status;

    private String description;

    private String image;

    private List<String> tags;

    private List<String> skills;

    private String duration;

    private String company;

    private Integer order;
}