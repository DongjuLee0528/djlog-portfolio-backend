package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CertificateRequest {
    @NotBlank(message = "자격증명은 필수입니다")
    private String name;

    private String issuer;

    private LocalDate issueDate;

    private String credentialId;
}