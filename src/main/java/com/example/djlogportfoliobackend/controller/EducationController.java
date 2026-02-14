package com.example.djlogportfoliobackend.controller;

import com.example.djlogportfoliobackend.dto.EducationRequest;
import com.example.djlogportfoliobackend.dto.EducationResponse;
import com.example.djlogportfoliobackend.service.EducationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 학력 정보 관리 REST API 컨트롤러
 * 학력 정보의 CRUD 작업을 위한 HTTP 엔드포인트를 제공합니다.
 */
@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    /**
     * 모든 학력 정보 조회
     * @return 학력 정보 리스트 (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<EducationResponse>> getAllEducations() {
        return ResponseEntity.ok(educationService.getAllEducations());
    }

    /**
     * 새로운 학력 정보 등록
     * @param request 학력 정보 등록 요청 DTO
     * @return 등록된 학력 정보 (201 Created)
     */
    @PostMapping
    public ResponseEntity<EducationResponse> createEducation(@Valid @RequestBody EducationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(educationService.createEducation(request));
    }

    /**
     * 학력 정보 수정
     * @param id 수정할 학력 정보 ID
     * @param request 학력 정보 수정 요청 DTO
     * @return 수정된 학력 정보 (200 OK) 또는 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<EducationResponse> updateEducation(@PathVariable UUID id, @Valid @RequestBody EducationRequest request) {
        try {
            return ResponseEntity.ok(educationService.updateEducation(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 학력 정보 삭제
     * @param id 삭제할 학력 정보 ID
     * @return 204 No Content 또는 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEducation(@PathVariable UUID id) {
        try {
            educationService.deleteEducation(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}