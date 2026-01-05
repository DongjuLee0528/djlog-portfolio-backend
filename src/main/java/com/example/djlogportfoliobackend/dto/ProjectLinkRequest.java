package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

@Data
public class ProjectLinkRequest {

    private UUID projectId;

    @NotBlank(message = "링크 이름은 필수입니다")
    private String label;

    @NotBlank(message = "링크 주소는 필수입니다")
    private String url;

    private String description;
}