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

@RestController
@RequestMapping("/api/certificates")
@RequiredArgsConstructor
public class CertificateController {

    private final CertificateService certificateService;

    @GetMapping
    public ResponseEntity<List<CertificateResponse>> getAllCertificates() {
        return ResponseEntity.ok(certificateService.getAllCertificates());
    }

    @PostMapping
    public ResponseEntity<CertificateResponse> createCertificate(@Valid @RequestBody CertificateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(certificateService.createCertificate(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CertificateResponse> updateCertificate(@PathVariable UUID id, @Valid @RequestBody CertificateRequest request) {
        try {
            return ResponseEntity.ok(certificateService.updateCertificate(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

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