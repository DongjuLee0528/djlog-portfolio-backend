package com.example.djlogportfoliobackend.controller;

import com.example.djlogportfoliobackend.dto.SkillRequest;
import com.example.djlogportfoliobackend.dto.SkillResponse;
import com.example.djlogportfoliobackend.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 기술 스택 정보 관리 REST API 컨트롤러
 * 기술 스택 정보의 CRUD 작업을 위한 HTTP 엔드포인트를 제공합니다.
 */
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    /**
     * 모든 기술 스택 정보 조회
     * @return 기술 스택 정보 리스트 (200 OK)
     */
    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    /**
     * 새로운 기술 스택 정보 등록
     * @param request 기술 스택 정보 등록 요청 DTO
     * @return 등록된 기술 스택 정보 (201 Created)
     */
    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(@Valid @RequestBody SkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkill(request));
    }

    /**
     * 기술 스택 정보 수정
     * @param id 수정할 기술 스택 정보 ID
     * @param request 기술 스택 정보 수정 요청 DTO
     * @return 수정된 기술 스택 정보 (200 OK) 또는 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> updateSkill(@PathVariable UUID id, @Valid @RequestBody SkillRequest request) {
        try {
            return ResponseEntity.ok(skillService.updateSkill(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 기술 스택 정보 삭제
     * @param id 삭제할 기술 스택 정보 ID
     * @return 204 No Content 또는 404 Not Found
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable UUID id) {
        try {
            skillService.deleteSkill(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}