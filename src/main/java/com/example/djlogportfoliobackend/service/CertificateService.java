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

/**
 * 자격증 정보 관리 서비스
 * 자격증 정보의 CRUD 작업을 처리하고 비즈니스 로직을 담당합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final ProfileRepository profileRepository;

    /**
     * 모든 자격증 정보 조회
     * 발급일순으로 내림차순 정렬하여 반환합니다.
     * @return 자격증 정보 리스트
     */
    public List<CertificateResponse> getAllCertificates() {
        return certificateRepository.findAllByOrderByIssueDateDesc().stream()
                .map(CertificateResponse::new)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 자격증 정보 등록
     * @param request 자격증 정보 등록 요청 DTO
     * @return 등록된 자격증 정보 응답 DTO
     * @throws RuntimeException 프로필이 존재하지 않을 때
     */
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

    /**
     * 자격증 정보 수정
     * @param id 수정할 자격증 정보 ID
     * @param request 자격증 정보 수정 요청 DTO
     * @return 수정된 자격증 정보 응답 DTO
     * @throws RuntimeException 자격증 정보를 찾을 수 없을 때
     */
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

    /**
     * 자격증 정보 삭제
     * @param id 삭제할 자격증 정보 ID
     * @throws RuntimeException 자격증 정보를 찾을 수 없을 때
     */
    @Transactional
    public void deleteCertificate(UUID id) {
        if (!certificateRepository.existsById(id)) {
            throw new RuntimeException("자격증 정보를 찾을 수 없습니다.");
        }
        certificateRepository.deleteById(id);
    }
}