package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.CertificateRequest;
import com.example.djlogportfoliobackend.dto.CertificateResponse;
import com.example.djlogportfoliobackend.entity.Certificate;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.exception.ResourceNotFoundException;
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
 * 자격증 데이터의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final ProfileRepository profileRepository;

    /**
     * 모든 자격증 목록 조회
     * 발급일을 기준으로 내림차순 정렬하여 반환합니다.
     *
     * @return 자격증 목록
     */
    public List<CertificateResponse> getAllCertificates() {
        return certificateRepository.findAllByOrderByIssueDateDesc().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    /**
     * 새로운 자격증 정보 생성
     *
     * @param request 자격증 생성 요청 데이터
     * @return 생성된 자격증 정보
     * @throws ResourceNotFoundException 프로필이 존재하지 않을 경우
     */
    @Transactional
    public CertificateResponse createCertificate(CertificateRequest request) {
        Profile profile = profileRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("프로필이 존재하지 않습니다."));

        Certificate certificate = new Certificate(
                request.getName(),
                request.getIssuer(),
                request.getIssueDate(),
                request.getCredentialId(),
                profile
        );

        Certificate savedCertificate = certificateRepository.save(certificate);
        return toResponse(savedCertificate);
    }

    /**
     * 기존 자격증 정보 수정
     *
     * @param id 수정할 자격증 ID
     * @param request 수정할 자격증 데이터
     * @return 수정된 자격증 정보
     * @throws ResourceNotFoundException 자격증 정보를 찾을 수 없을 경우
     */
    @Transactional
    public CertificateResponse updateCertificate(UUID id, CertificateRequest request) {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("자격증 정보를 찾을 수 없습니다."));

        certificate.setName(request.getName());
        certificate.setIssuer(request.getIssuer());
        certificate.setIssueDate(request.getIssueDate());
        certificate.setCredentialId(request.getCredentialId());

        return toResponse(certificate);
    }

    /**
     * 자격증 정보 삭제
     *
     * @param id 삭제할 자격증 ID
     * @throws ResourceNotFoundException 자격증 정보를 찾을 수 없을 경우
     */
    @Transactional
    public void deleteCertificate(UUID id) {
        if (!certificateRepository.existsById(id)) {
            throw new ResourceNotFoundException("자격증 정보를 찾을 수 없습니다.");
        }
        certificateRepository.deleteById(id);
    }

    /**
     * 엔티티를 응답 DTO로 변환
     *
     * @param certificate 자격증 엔티티
     * @return 자격증 응답 DTO
     */
    private CertificateResponse toResponse(Certificate certificate) {
        return new CertificateResponse(certificate);
    }
}