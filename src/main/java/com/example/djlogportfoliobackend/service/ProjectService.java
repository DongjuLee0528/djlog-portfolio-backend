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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
        Project project = convertToEntity(request);
        Project savedProject = projectRepository.save(project);
        return convertToResponse(savedProject);
    }

    @Transactional
    public ProjectResponse updateProject(UUID id, ProjectRequest request) {
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
                    return convertToResponse(savedProject);
                })
                .orElseThrow(() -> new RuntimeException("프로젝트를 찾을 수 없습니다. ID: " + id));
    }

    @Transactional
    public void deleteProject(UUID id) {
        projectRepository.deleteById(id);
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