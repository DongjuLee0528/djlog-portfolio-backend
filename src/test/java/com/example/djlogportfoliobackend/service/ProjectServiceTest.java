package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.ProjectRequest;
import com.example.djlogportfoliobackend.dto.ProjectResponse;
import com.example.djlogportfoliobackend.entity.Project;
import com.example.djlogportfoliobackend.entity.ProjectStatus;
import com.example.djlogportfoliobackend.exception.ResourceNotFoundException;
import com.example.djlogportfoliobackend.repository.ProjectRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ProjectService 단위 테스트")
class ProjectServiceTest {

    @Mock
    private ProjectRepository projectRepository;

    @InjectMocks
    private ProjectService projectService;

    private Project testProject;
    private ProjectRequest projectRequest;

    @BeforeEach
    void setUp() {
        testProject = new Project(
                "Test Project",
                "Web",
                ProjectStatus.PUBLISHED,
                "Test Description",
                "test-image.jpg",
                "Java,Spring",
                "2023-01-01 ~ 2023-03-01",
                "Test Company",
                1
        );
        testProject.setId(UUID.randomUUID());

        projectRequest = new ProjectRequest();
        projectRequest.setTitle("New Project");
        projectRequest.setCategory("Mobile");
        projectRequest.setStatus(ProjectStatus.DRAFT);
        projectRequest.setDescription("New Description");
        projectRequest.setImage("new-image.jpg");
        projectRequest.setTags("Kotlin,Android");
        projectRequest.setDuration("2023-04-01 ~ 2023-06-01");
        projectRequest.setCompany("New Company");
        projectRequest.setOrder(2);
    }

    @Test
    @DisplayName("전체 프로젝트 목록 조회 성공")
    void getAllProjects_Success() {
        // Given
        List<Project> projects = Arrays.asList(testProject);
        when(projectRepository.findAllWithDetails()).thenReturn(projects);

        // When
        List<ProjectResponse> result = projectService.getAllProjects();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTitle()).isEqualTo(testProject.getTitle());
        verify(projectRepository).findAllWithDetails();
    }

    @Test
    @DisplayName("공개된 프로젝트 목록 조회 성공")
    void getPublishedProjects_Success() {
        // Given
        List<Project> publishedProjects = Arrays.asList(testProject);
        when(projectRepository.findByStatusWithDetails(ProjectStatus.PUBLISHED))
                .thenReturn(publishedProjects);

        // When
        List<ProjectResponse> result = projectService.getPublishedProjects();

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getStatus()).isEqualTo(ProjectStatus.PUBLISHED);
        verify(projectRepository).findByStatusWithDetails(ProjectStatus.PUBLISHED);
    }

    @Test
    @DisplayName("ID로 프로젝트 조회 성공")
    void getProjectById_Success() {
        // Given
        UUID projectId = testProject.getId();
        when(projectRepository.findByIdWithDetails(projectId))
                .thenReturn(Optional.of(testProject));

        // When
        Optional<ProjectResponse> result = projectService.getProjectById(projectId);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(projectId);
        assertThat(result.get().getTitle()).isEqualTo(testProject.getTitle());
        verify(projectRepository).findByIdWithDetails(projectId);
    }

    @Test
    @DisplayName("ID로 프로젝트 조회 실패 - 존재하지 않는 프로젝트")
    void getProjectById_NotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(projectRepository.findByIdWithDetails(nonExistentId))
                .thenReturn(Optional.empty());

        // When
        Optional<ProjectResponse> result = projectService.getProjectById(nonExistentId);

        // Then
        assertThat(result).isEmpty();
        verify(projectRepository).findByIdWithDetails(nonExistentId);
    }

    @Test
    @DisplayName("새 프로젝트 생성 성공")
    void createProject_Success() {
        // Given
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        ProjectResponse result = projectService.createProject(projectRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(projectRequest.getTitle());
        assertThat(result.getCategory()).isEqualTo(projectRequest.getCategory());
        verify(projectRepository).save(any(Project.class));
    }

    @Test
    @DisplayName("프로젝트 업데이트 성공")
    void updateProject_Success() {
        // Given
        UUID projectId = testProject.getId();
        when(projectRepository.findById(projectId)).thenReturn(Optional.of(testProject));
        when(projectRepository.save(any(Project.class))).thenReturn(testProject);

        // When
        ProjectResponse result = projectService.updateProject(projectId, projectRequest);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getTitle()).isEqualTo(projectRequest.getTitle());
        verify(projectRepository).findById(projectId);
        verify(projectRepository).save(testProject);
    }

    @Test
    @DisplayName("프로젝트 업데이트 실패 - 존재하지 않는 프로젝트")
    void updateProject_NotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(projectRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        // When & Then
        assertThatThrownBy(() -> projectService.updateProject(nonExistentId, projectRequest))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("프로젝트를 찾을 수 없습니다");

        verify(projectRepository).findById(nonExistentId);
        verify(projectRepository, never()).save(any(Project.class));
    }

    @Test
    @DisplayName("프로젝트 삭제 성공")
    void deleteProject_Success() {
        // Given
        UUID projectId = testProject.getId();
        when(projectRepository.existsById(projectId)).thenReturn(true);

        // When
        projectService.deleteProject(projectId);

        // Then
        verify(projectRepository).existsById(projectId);
        verify(projectRepository).deleteById(projectId);
    }

    @Test
    @DisplayName("프로젝트 삭제 실패 - 존재하지 않는 프로젝트")
    void deleteProject_NotFound() {
        // Given
        UUID nonExistentId = UUID.randomUUID();
        when(projectRepository.existsById(nonExistentId)).thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> projectService.deleteProject(nonExistentId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("프로젝트를 찾을 수 없습니다");

        verify(projectRepository).existsById(nonExistentId);
        verify(projectRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("카테고리별 프로젝트 조회 성공")
    void getProjectsByCategory_Success() {
        // Given
        String category = "Web";
        List<Project> categoryProjects = Arrays.asList(testProject);
        when(projectRepository.findByCategoryIgnoreCaseOrderByOrderAscTitleAsc(category))
                .thenReturn(categoryProjects);

        // When
        List<ProjectResponse> result = projectService.getProjectsByCategory(category);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory()).isEqualToIgnoringCase(category);
        verify(projectRepository).findByCategoryIgnoreCaseOrderByOrderAscTitleAsc(category);
    }

    @Test
    @DisplayName("태그별 프로젝트 조회 성공")
    void getProjectsByTag_Success() {
        // Given
        String tag = "Java";
        List<Project> tagProjects = Arrays.asList(testProject);
        when(projectRepository.findByTagsContainingIgnoreCaseOrderByOrderAscTitleAsc(tag))
                .thenReturn(tagProjects);

        // When
        List<ProjectResponse> result = projectService.getProjectsByTag(tag);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getTags()).containsIgnoringCase(tag);
        verify(projectRepository).findByTagsContainingIgnoreCaseOrderByOrderAscTitleAsc(tag);
    }
}