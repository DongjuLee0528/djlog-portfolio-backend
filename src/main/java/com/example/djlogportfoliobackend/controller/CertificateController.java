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
 * 자격증/인증서 정보 관리를 위한 REST API 컨트롤러
 * 자격증의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 *
 * @author DongJu Lee
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    /**
     * 모든 자격증 정보 목록을 조회합니다.
     *
     * @return 자격증 목록과 HTTP 200 OK 상태
     */
    @GetMapping
    public ResponseEntity<List<CertificateResponse>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    /**
     * 새로운 자격증 정보를 생성합니다.
     *
     * @param request 생성할 자격증 정보
     * @return 생성된 자격증 정보와 HTTP 201 CREATED 상태
     */
    @PostMapping
    public ResponseEntity<CertificateResponse> createCertificate(@Valid @RequestBody CertificateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificateService.createCertificate(request));
    }

    /**
     * 기존 자격증 정보를 수정합니다.
     *
     * @param id 수정할 자격증의 UUID
     * @param request 수정할 자격증 정보
     * @return 수정된 자격증 정보와 HTTP 200 OK 상태
     */
    @PutMapping("/{id}")
    public ResponseEntity<CertificateResponse> updateCertificate(@PathVariable UUID id, @Valid @RequestBody CertificateRequest request) {
        return ResponseEntity.ok(certificateService.updateCertificate(id, request));
    }

    /**
     * 자격증 정보를 삭제합니다.
     *
     * @param id 삭제할 자격증의 UUID
     * @return HTTP 204 NO CONTENT 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCertificate(@PathVariable UUID id) {
        certificateService.deleteCertificate(id);
        return ResponseEntity.noContent().build();
    }
}