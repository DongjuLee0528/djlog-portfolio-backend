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
 * 기술 스택 관리를 위한 REST API 컨트롤러
 * 기술 스택의 생성, 조회, 수정, 삭제 기능을 제공합니다.
 *
 * @author DongJu Lee
 * @version 1.0
 * @since 1.0
 */
@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    /**
     * 모든 기술 스택 목록을 조회합니다.
     *
     * @return 기술 스택 목록과 HTTP 200 OK 상태
     */
    @GetMapping
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    /**
     * 새로운 기술 스택을 생성합니다.
     *
     * @param request 생성할 기술 스택 정보
     * @return 생성된 기술 스택 정보와 HTTP 201 CREATED 상태
     */
    @PostMapping
    public ResponseEntity<SkillResponse> createSkill(@Valid @RequestBody SkillRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(skillService.createSkill(request));
    }

    /**
     * 기존 기술 스택을 수정합니다.
     *
     * @param id 수정할 기술 스택의 UUID
     * @param request 수정할 기술 스택 정보
     * @return 수정된 기술 스택 정보와 HTTP 200 OK 상태
     */
    @PutMapping("/{id}")
    public ResponseEntity<SkillResponse> updateSkill(@PathVariable UUID id, @Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(skillService.updateSkill(id, request));
    }

    /**
     * 기술 스택을 삭제합니다.
     *
     * @param id 삭제할 기술 스택의 UUID
     * @return HTTP 204 NO CONTENT 상태
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable UUID id) {
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }
}