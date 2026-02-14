package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.Certificate;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 자격증 정보 응답 DTO
 * 클라이언트에게 자격증 정보를 전달하기 위한 데이터 전송 객체
 */
@Data
public class CertificateResponse {
    private UUID id;
    private String name;
    private String issuer;
    private LocalDate issueDate;
    private String credentialId;

    /**
     * Certificate 엔티티로부터 응답 DTO를 생성하는 생성자
     * @param certificate 변환할 Certificate 엔티티
     */
    public CertificateResponse(Certificate certificate) {
        this.id = certificate.getId();
        this.name = certificate.getName();
        this.issuer = certificate.getIssuer();
        this.issueDate = certificate.getIssueDate();
        this.credentialId = certificate.getCredentialId();
    }
}