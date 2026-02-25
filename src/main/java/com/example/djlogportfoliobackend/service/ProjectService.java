package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.ProjectRequest;
import com.example.djlogportfoliobackend.dto.ProjectResponse;
import com.example.djlogportfoliobackend.dto.ProjectLinkResponse;
import com.example.djlogportfoliobackend.dto.ProjectQnAResponse;
import com.example.djlogportfoliobackend.dto.ProjectSkillResponse;
import com.example.djlogportfoliobackend.entity.Project;
import com.example.djlogportfoliobackend.entity.ProjectLink;
import com.example.djlogportfoliobackend.entity.ProjectQnA;
import com.example.djlogportfoliobackend.entity.ProjectSkill;
import com.example.djlogportfoliobackend.entity.ProjectStatus;
import com.example.djlogportfoliobackend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 프로젝트 관리 서비스
 * 프로젝트에 대한 CRUD 작업과 다양한 조회 기능을 제공합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    /**
     * 전체 프로젝트 목록 조회
     * 정렬 순서와 제목 순으로 정렬된 목록을 반환합니다.
     *
     * @return 전체 프로젝트 목록
     */
    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAllByOrderByOrderAscTitleAsc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 공개된 프로젝트 목록 조회
     * PUBLISHED 상태의 프로젝트만 조회합니다.
     *
     * @return 공개된 프로젝트 목록
     */
    public List<ProjectResponse> getPublishedProjects() {
        return projectRepository.findByStatusOrderByOrderAscTitleAsc(ProjectStatus.PUBLISHED).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 카테고리별 프로젝트 조회
     * 대소문자를 구분하지 않고 해당 카테고리의 프로젝트를 조회합니다.
     *
     * @param category 조회할 카테고리명
     * @return 카테고리에 해당하는 프로젝트 목록
     */
    public List<ProjectResponse> getProjectsByCategory(String category) {
        return projectRepository.findByCategoryIgnoreCaseOrderByOrderAscTitleAsc(category).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * 태그별 프로젝트 조회
     * 대소문자를 구분하지 않고 해당 태그를 포함한 프로젝트를 조회합니다.
     *
     * @param tag 검색할 태그
     * @return 해당 태그를 포함한 프로젝트 목록
     */
    public List<ProjectResponse> getProjectsByTag(String tag) {
        return projectRepository.findByTagsContainingIgnoreCaseOrderByOrderAscTitleAsc(tag).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * ID로 프로젝트 단건 조회
     *
     * @param id 조회할 프로젝트 ID
     * @return 프로젝트 정보 (Optional)
     */
    public Optional<ProjectResponse> getProjectById(UUID id) {
        return projectRepository.findById(id)
                .map(this::convertToResponse);
    }

    /**
     * 새로운 프로젝트 생성
     * 요청 데이터를 바탕으로 새로운 프로젝트를 생성합니다.
     *
     * @param request 프로젝트 생성 요청 데이터
     * @return 생성된 프로젝트 정보
     * @throws Exception 프로젝트 생성 실패 시
     */
    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        String traceId = MDC.get("traceId");
        log.info("[PROJECT] Creating new project - TraceId: {} - Title: {}", traceId, request.getTitle());

        try {
            Project project = convertToEntity(request);
            
            // 연관 데이터 설정 (Skills, Links, QnA)
            updateProjectRelations(project, request);
            
            Project savedProject = projectRepository.save(project);
            log.info("[PROJECT] Project created successfully - TraceId: {} - ID: {} - Title: {}",
                    traceId, savedProject.getId(), savedProject.getTitle());
            return convertToResponse(savedProject);
        } catch (Exception e) {
            log.error("[PROJECT] Failed to create project - TraceId: {} - Title: {} - Error: {}",
                    traceId, request.getTitle(), e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 기존 프로젝트 수정
     * ID로 프로젝트를 찾아 요청 데이터로 업데이트합니다.
     *
     * @param id 수정할 프로젝트 ID
     * @param request 수정할 프로젝트 데이터
     * @return 수정된 프로젝트 정보
     * @throws RuntimeException 프로젝트를 찾을 수 없거나 수정 실패 시
     */
    @Transactional
    public ProjectResponse updateProject(UUID id, ProjectRequest request) {
        String traceId = MDC.get("traceId");
        log.info("[PROJECT] Updating project - TraceId: {} - ID: {} - Title: {}", traceId, id, request.getTitle());

        try {
            return projectRepository.findById(id)
                    .map(project -> {
                        // 기본 정보 업데이트
                        project.setTitle(request.getTitle());
                        project.setCategory(request.getCategory());
                        project.setStatus(request.getStatus() != null ? request.getStatus() : ProjectStatus.DRAFT);
                        project.setDescription(request.getDescription());
                        project.setImage(request.getImage());
                        project.setTags(request.getTags());
                        project.setDuration(request.getDuration());
                        project.setCompany(request.getCompany());
                        project.setOrder(request.getOrder() != null ? request.getOrder() : 0);
                        
                        // 연관 데이터 업데이트 (Skills, Links, QnA)
                        // 기존 데이터를 모두 지우고 새로 추가하는 방식 (orphanRemoval 동작)
                        project.getSkills().clear();
                        project.getLinks().clear();
                        project.getQnaList().clear();
                        
                        updateProjectRelations(project, request);
                        
                        Project savedProject = projectRepository.save(project);
                        log.info("[PROJECT] Project updated successfully - TraceId: {} - ID: {} - Title: {}",
                                traceId, savedProject.getId(), savedProject.getTitle());
                        return convertToResponse(savedProject);
                    })
                    .orElseThrow(() -> {
                        log.warn("[PROJECT] Project not found for update - TraceId: {} - ID: {}", traceId, id);
                        return new RuntimeException("프로젝트를 찾을 수 없습니다. ID: " + id);
                    });
        } catch (Exception e) {
            log.error("[PROJECT] Failed to update project - TraceId: {} - ID: {} - Error: {}",
                    traceId, id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 프로젝트 삭제
     * ID로 프로젝트를 찾아 삭제합니다.
     *
     * @param id 삭제할 프로젝트 ID
     * @throws RuntimeException 프로젝트를 찾을 수 없거나 삭제 실패 시
     */
    @Transactional
    public void deleteProject(UUID id) {
        String traceId = MDC.get("traceId");
        log.info("[PROJECT] Deleting project - TraceId: {} - ID: {}", traceId, id);

        try {
            if (!projectRepository.existsById(id)) {
                log.warn("[PROJECT] Project not found for deletion - TraceId: {} - ID: {}", traceId, id);
                throw new RuntimeException("프로젝트를 찾을 수 없습니다. ID: " + id);
            }

            projectRepository.deleteById(id);
            log.info("[PROJECT] Project deleted successfully - TraceId: {} - ID: {}", traceId, id);
        } catch (Exception e) {
            log.error("[PROJECT] Failed to delete project - TraceId: {} - ID: {} - Error: {}",
                    traceId, id, e.getMessage(), e);
            throw e;
        }
    }

    /**
     * 프로젝트 연관 데이터(Skills, Links, QnA) 업데이트
     * 요청 DTO의 데이터를 엔티티 리스트로 변환하여 프로젝트에 추가합니다.
     */
    private void updateProjectRelations(Project project, ProjectRequest request) {
        // Skills
        if (request.getSkills() != null) {
            request.getSkills().forEach(skillReq -> {
                ProjectSkill skill = new ProjectSkill();
                skill.setName(skillReq.getName());
                skill.setCategory(skillReq.getCategory());
                skill.setProject(project);
                project.getSkills().add(skill);
            });
        }

        // Links
        if (request.getLinks() != null) {
            request.getLinks().forEach(linkReq -> {
                ProjectLink link = new ProjectLink();
                link.setLabel(linkReq.getLabel());
                link.setUrl(linkReq.getUrl());
                link.setDescription(linkReq.getDescription());
                link.setProject(project);
                project.getLinks().add(link);
            });
        }

        // QnA
        if (request.getQnaList() != null) {
            request.getQnaList().forEach(qnaReq -> {
                ProjectQnA qna = new ProjectQnA();
                qna.setQuestion(qnaReq.getQuestion());
                qna.setAnswer(qnaReq.getAnswer());
                qna.setProject(project);
                project.getQnaList().add(qna);
            });
        }
    }

    /**
     * 요청 DTO를 엔티티로 변환
     *
     * @param request 프로젝트 요청 DTO
     * @return 프로젝트 엔티티
     */
    private Project convertToEntity(ProjectRequest request) {
        return new Project(
                request.getTitle(),
                request.getCategory(),
                request.getStatus(),
                request.getDescription(),
                request.getImage(),
                request.getTags(),
                request.getDuration(),
                request.getCompany(),
                request.getOrder()
        );
    }

    /**
     * 엔티티를 응답 DTO로 변환
     * 연관된 링크와 Q&A 정보도 함께 변환합니다.
     *
     * @param project 프로젝트 엔티티
     * @return 프로젝트 응답 DTO
     */
    private ProjectResponse convertToResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setCategory(project.getCategory());
        response.setStatus(project.getStatus());
        response.setDescription(project.getDescription());
        response.setImage(project.getImage());
        response.setTags(project.getTags());
        response.setDuration(project.getDuration());
        response.setCompany(project.getCompany());
        response.setOrder(project.getOrder());

        // Convert ProjectSkill entities to DTOs
        if (project.getSkills() != null) {
            response.setSkills(project.getSkills().stream()
                    .map(this::convertToSkillResponse)
                    .collect(Collectors.toList()));
        }

        // Convert ProjectLink entities to DTOs
        if (project.getLinks() != null) {
            response.setLinks(project.getLinks().stream()
                    .map(this::convertToLinkResponse)
                    .collect(Collectors.toList()));
        }

        // Convert ProjectQnA entities to DTOs
        if (project.getQnaList() != null) {
            response.setQnaList(project.getQnaList().stream()
                    .map(this::convertToQnAResponse)
                    .collect(Collectors.toList()));
        }

        return response;
    }

    /**
     * 프로젝트 링크 엔티티를 DTO로 변환
     *
     * @param link 프로젝트 링크 엔티티
     * @return 프로젝트 링크 DTO
     */
    private ProjectLinkResponse convertToLinkResponse(ProjectLink link) {
        ProjectLinkResponse response = new ProjectLinkResponse();
        response.setId(link.getId());
        response.setProjectId(link.getProject().getId());
        response.setLabel(link.getLabel());
        response.setUrl(link.getUrl());
        response.setDescription(link.getDescription());
        return response;
    }

    /**
     * 프로젝트 Q&A 엔티티를 DTO로 변환
     *
     * @param qna 프로젝트 Q&A 엔티티
     * @return 프로젝트 Q&A DTO
     */
    private ProjectQnAResponse convertToQnAResponse(ProjectQnA qna) {
        ProjectQnAResponse response = new ProjectQnAResponse();
        response.setId(qna.getId());
        response.setProjectId(qna.getProject().getId());
        response.setQuestion(qna.getQuestion());
        response.setAnswer(qna.getAnswer());
        return response;
    }

    /**
     * 프로젝트 스킬 엔티티를 DTO로 변환
     *
     * @param skill 프로젝트 스킬 엔티티
     * @return 프로젝트 스킬 DTO
     */
    private ProjectSkillResponse convertToSkillResponse(ProjectSkill skill) {
        ProjectSkillResponse response = new ProjectSkillResponse();
        response.setId(skill.getId());
        response.setName(skill.getName());
        response.setCategory(skill.getCategory());
        return response;
    }
}