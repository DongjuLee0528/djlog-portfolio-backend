package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

/**
 * 자격증 정보 등록/수정 요청 DTO
 * 클라이언트로부터 자격증 정보를 받기 위한 데이터 전송 객체
 */
@Data
public class CertificateRequest {
    @NotBlank(message = "자격증명은 필수입니다")
    private String name;

    private String issuer;

    private LocalDate issueDate;

    private String credentialId;
}