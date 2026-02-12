package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.CertificateRequest;
import com.example.djlogportfoliobackend.dto.CertificateResponse;
import com.example.djlogportfoliobackend.entity.Certificate;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.repository.CertificateRepository;
import com.example.djlogportfoliobackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final ProfileRepository profileRepository;

    public List<CertificateResponse> getAllCertificates() {
        return certificateRepository.findAllByOrderByIssueDateDesc().stream()
                .map(CertificateResponse::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public CertificateResponse createCertificate(CertificateRequest request) {
        Profile profile = profileRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new RuntimeException("프로필이 존재하지 않습니다."));

        Certificate certificate = new Certificate(
                request.getName(),
                request.getIssuer(),
                request.getIssueDate(),
                request.getCredentialId(),
                profile
        );

        Certificate savedCertificate = certificateRepository.save(certificate);
        return new CertificateResponse(savedCertificate);
    }

    @Transactional
    public CertificateResponse updateCertificate(UUID id, CertificateRequest request) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("자격증 정보를 찾을 수 없습니다."));

        certificate.setName(request.getName());
        certificate.setIssuer(request.getIssuer());
        certificate.setIssueDate(request.getIssueDate());
        certificate.setCredentialId(request.getCredentialId());

        return new CertificateResponse(certificate);
    }

    @Transactional
    public void deleteCertificate(UUID id) {
        if (!certificateRepository.existsById(id)) {
            throw new RuntimeException("자격증 정보를 찾을 수 없습니다.");
        }
        certificateRepository.deleteById(id);
    }
}