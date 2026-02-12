package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.Certificate;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CertificateResponse {
    private UUID id;
    private String name;
    private String issuer;
    private LocalDate issueDate;
    private String credentialId;

    public CertificateResponse(Certificate certificate) {
        this.id = certificate.getId();
        this.name = certificate.getName();
        this.issuer = certificate.getIssuer();
        this.issueDate = certificate.getIssueDate();
        this.credentialId = certificate.getCredentialId();
    }
}