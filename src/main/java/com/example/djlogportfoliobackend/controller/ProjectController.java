package com.example.djlogportfoliobackend.controller;

import com.example.djlogportfoliobackend.dto.ProjectQnAOrderBulkUpdateRequest;
import com.example.djlogportfoliobackend.dto.ProjectQnAResponse;
import com.example.djlogportfoliobackend.dto.ProjectRequest;
import com.example.djlogportfoliobackend.dto.ProjectResponse;
import com.example.djlogportfoliobackend.service.FileUploadService;
import com.example.djlogportfoliobackend.service.ProjectService;
import com.example.djlogportfoliobackend.service.SecurityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 프로젝트 관련 REST API 컨트롤러
 * 프로젝트의 CRUD 기능과 조회 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final SecurityService securityService;
    private final FileUploadService fileUploadService;

    /**
     * 프로젝트 목록을 조회합니다.
     *
     * @param status 프로젝트 상태 (published: 발행된 프로젝트, all: 모든 프로젝트)
     * @param category 프로젝트 카테고리 필터
     * @param tag 프로젝트 태그 필터
     * @return 프로젝트 목록
     */
    @GetMapping
    public ResponseEntity<List<ProjectResponse>> getAllProjects(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String tag) {

        // 권한에 따른 상태 검증
        status = securityService.validateProjectStatusAccess(status);

        List<ProjectResponse> projects;

        if ("published".equalsIgnoreCase(status)) {
            projects = projectService.getPublishedProjects();
        } else if ("all".equalsIgnoreCase(status)) {
            projects = projectService.getAllProjects();
        } else if (category != null && !category.trim().isEmpty()) {
            projects = projectService.getProjectsByCategory(category);
        } else if (tag != null && !tag.trim().isEmpty()) {
            projects = projectService.getProjectsByTag(tag);
        } else {
            projects = projectService.getPublishedProjects();
        }

        return ResponseEntity.ok(projects);
    }

    /**
     * 프로젝트 이미지 파일 업로드 API
     *
     * @param file 업로드할 이미지 파일
     * @return 프로젝트 image 필드에 바로 저장 가능한 경로
     * @throws IOException 파일 저장 중 오류 발생 시
     */
    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> uploadProjectImage(@RequestParam("file") MultipartFile file) throws IOException {
        String imagePath = fileUploadService.uploadFile(file);

        Map<String, String> response = new HashMap<>();
        response.put("image", imagePath);
        response.put("url", imagePath);
        response.put("path", imagePath);

        return ResponseEntity.ok(response);
    }

    /**
     * 특정 프로젝트를 ID로 조회합니다.
     *
     * @param id 프로젝트 ID
     * @return 프로젝트 정보 또는 404 Not Found
     */
    @GetMapping("/{id}")
    public ResponseEntity<ProjectResponse> getProjectById(@PathVariable UUID id) {
        return projectService.getProjectById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * 특정 프로젝트의 Q&A 목록을 표시 순서대로 조회합니다.
     *
     * @param projectId 프로젝트 ID
     * @return 정렬된 Q&A 목록
     */
    @GetMapping("/{projectId}/qna")
    public ResponseEntity<List<ProjectQnAResponse>> getProjectQnAs(@PathVariable UUID projectId) {
        return ResponseEntity.ok(projectService.getProjectQnAs(projectId));
    }

    /**
     * 새로운 프로젝트를 생성합니다.
     *
     * @param request 프로젝트 생성 요청 정보
     * @return 생성된 프로젝트 정보
     */
    @PostMapping
    public ResponseEntity<ProjectResponse> createProject(@Valid @RequestBody ProjectRequest request) {
        ProjectResponse createdProject = projectService.createProject(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProject);
    }

    /**
     * 기존 프로젝트를 수정합니다.
     *
     * @param id 수정할 프로젝트 ID
     * @param request 프로젝트 수정 요청 정보
     * @return 수정된 프로젝트 정보 또는 404 Not Found
     */
    @PutMapping("/{id}")
    public ResponseEntity<ProjectResponse> updateProject(@PathVariable UUID id, @Valid @RequestBody ProjectRequest request) {
        ProjectResponse updatedProject = projectService.updateProject(id, request);
        return ResponseEntity.ok(updatedProject);
    }

    /**
     * 특정 프로젝트의 Q&A 표시 순서를 일괄 수정합니다.
     *
     * @param projectId 프로젝트 ID
     * @param request 순서 변경 요청
     * @return 정렬된 Q&A 목록
     */
    @PutMapping("/{projectId}/qna/display-order")
    public ResponseEntity<List<ProjectQnAResponse>> updateProjectQnADisplayOrders(
            @PathVariable UUID projectId,
            @Valid @RequestBody ProjectQnAOrderBulkUpdateRequest request) {
        return ResponseEntity.ok(projectService.updateProjectQnADisplayOrders(projectId, request));
    }

    /**
     * 특정 프로젝트를 삭제합니다.
     *
     * @param id 삭제할 프로젝트 ID
     * @return 204 No Content
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

}
