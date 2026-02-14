package com.example.djlogportfoliobackend.controller;

import com.example.djlogportfoliobackend.dto.CertificateRequest;
import com.example.djlogportfoliobackend.dto.CertificateResponse;
import com.example.djlogportfoliobackend.service.CertificateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 자격증 정보 관리 REST API 컨트롤러
 * 자격증 정보의 CRUD 작업을 위한 HTTP 엔드포인트를 제공합니다.
 */
@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    /**
     * 모든 자격증 정보 조회
     * @return 자격증 정보 리스트 (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<CertificateResponse>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    /**
     * 새로운 자격증 정보 등록
     * @param request 자격증 정보 등록 요청 DTO
     * @return 등록된 자격증 정보 (201 Created)
     */
    @PostMapping
    public ResponseEntity<CertificateResponse> createCertificate(@Valid @RequestBody CertificateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificateService.createCertificate(request));
    }

    /**
     * 자격증 정보 수정
     * @param id 수정할 자격증 정보 ID
     * @param request 자격증 정보 수정 요청 DTO
     * @return 수정된 자격증 정보 (200 OK) 또는 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<CertificateResponse> updateCertificate(@PathVariable UUID id, @Valid @RequestBody CertificateRequest request) {
        try {
            return ResponseEntity.ok(certificateService.updateCertificate(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 자격증 정보 삭제
     * @param id 삭제할 자격증 정보 ID
     * @return 204 No Content 또는 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable UUID id) {
        try {
            certificateService.deleteCertificate(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}