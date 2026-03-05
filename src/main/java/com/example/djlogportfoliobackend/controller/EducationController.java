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
 * 학력 정보 관리를 위한 REST API 컨트롤러
 * 학력 정보의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 *
 * @author DongJu Lee
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/educations")
@RequiredArgsConstructor
public class EducationController {

    private final EducationService educationService;

    /**
     * 모든 학력 정보 목록을 조회합니다.
     *
     * @return 학력 정보 목록과 HTTP 200 OK 상태
     */
    @GetMapping
    public ResponseEntity<List<EducationResponse>> getAllEducations() {
        return ResponseEntity.ok(educationService.getAllEducations());
    }

    /**
     * 새로운 학력 정보를 생성합니다.
     *
     * @param request 생성할 학력 정보
     * @return 생성된 학력 정보와 HTTP 201 CREATED 상태
     */
    @PostMapping
    public ResponseEntity<EducationResponse> createEducation(@Valid @RequestBody EducationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(educationService.createEducation(request));
    }

    /**
     * 기존 학력 정보를 수정합니다.
     *
     * @param id 수정할 학력 정보의 UUID
     * @param request 수정할 학력 정보
     * @return 수정된 학력 정보와 HTTP 200 OK 상태
     */
    @PutMapping("/{id}")
    public ResponseEntity<EducationResponse> updateEducation(@PathVariable UUID id, @Valid @RequestBody EducationRequest request) {
        return ResponseEntity.ok(educationService.updateEducation(id, request));
    }

    /**
     * 학력 정보를 삭제합니다.
     *
     * @param id 삭제할 학력 정보의 UUID
     * @return HTTP 204 NO CONTENT 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEducation(@PathVariable UUID id) {
        educationService.deleteEducation(id);
        return ResponseEntity.noContent().build();
    }
}