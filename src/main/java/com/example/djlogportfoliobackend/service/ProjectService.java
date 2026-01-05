package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.ProjectRequest;
import com.example.djlogportfoliobackend.dto.ProjectResponse;
import com.example.djlogportfoliobackend.dto.ProjectLinkResponse;
import com.example.djlogportfoliobackend.dto.ProjectQnAResponse;
import com.example.djlogportfoliobackend.entity.Project;
import com.example.djlogportfoliobackend.entity.ProjectLink;
import com.example.djlogportfoliobackend.entity.ProjectQnA;
import com.example.djlogportfoliobackend.entity.ProjectStatus;
import com.example.djlogportfoliobackend.repository.ProjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProjectService {

    private final ProjectRepository projectRepository;

    public List<ProjectResponse> getAllProjects() {
        return projectRepository.findAllByOrderByOrderAscTitleAsc().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProjectResponse> getPublishedProjects() {
        return projectRepository.findByStatusOrderByOrderAscTitleAsc(ProjectStatus.PUBLISHED).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProjectResponse> getProjectsByCategory(String category) {
        return projectRepository.findByCategoryIgnoreCaseOrderByOrderAscTitleAsc(category).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<ProjectResponse> getProjectsByTag(String tag) {
        return projectRepository.findByTagsContainingIgnoreCaseOrderByOrderAscTitleAsc(tag).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Optional<ProjectResponse> getProjectById(UUID id) {
        return projectRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Transactional
    public ProjectResponse createProject(ProjectRequest request) {
        String traceId = MDC.get("traceId");
        log.info("[PROJECT] Creating new project - TraceId: {} - Title: {}", traceId, request.getTitle());

        try {
            Project project = convertToEntity(request);
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

    @Transactional
    public ProjectResponse updateProject(UUID id, ProjectRequest request) {
        String traceId = MDC.get("traceId");
        log.info("[PROJECT] Updating project - TraceId: {} - ID: {} - Title: {}", traceId, id, request.getTitle());

        try {
            return projectRepository.findById(id)
                    .map(project -> {
                        project.setTitle(request.getTitle());
                        project.setCategory(request.getCategory());
                        project.setStatus(request.getStatus() != null ? request.getStatus() : ProjectStatus.DRAFT);
                        project.setDescription(request.getDescription());
                        project.setImage(request.getImage());
                        project.setTags(request.getTags());
                        project.setOrder(request.getOrder() != null ? request.getOrder() : 0);
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

    private Project convertToEntity(ProjectRequest request) {
        return new Project(
                request.getTitle(),
                request.getCategory(),
                request.getStatus(),
                request.getDescription(),
                request.getImage(),
                request.getTags(),
                request.getOrder()
        );
    }

    private ProjectResponse convertToResponse(Project project) {
        ProjectResponse response = new ProjectResponse();
        response.setId(project.getId());
        response.setTitle(project.getTitle());
        response.setCategory(project.getCategory());
        response.setStatus(project.getStatus());
        response.setDescription(project.getDescription());
        response.setImage(project.getImage());
        response.setTags(project.getTags());
        response.setOrder(project.getOrder());

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

    private ProjectLinkResponse convertToLinkResponse(ProjectLink link) {
        ProjectLinkResponse response = new ProjectLinkResponse();
        response.setId(link.getId());
        response.setProjectId(link.getProject().getId());
        response.setLabel(link.getLabel());
        response.setUrl(link.getUrl());
        response.setDescription(link.getDescription());
        return response;
    }

    private ProjectQnAResponse convertToQnAResponse(ProjectQnA qna) {
        ProjectQnAResponse response = new ProjectQnAResponse();
        response.setId(qna.getId());
        response.setProjectId(qna.getProject().getId());
        response.setQuestion(qna.getQuestion());
        response.setAnswer(qna.getAnswer());
        return response;
    }
}