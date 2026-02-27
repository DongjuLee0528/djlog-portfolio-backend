package com.example.djlogportfoliobackend.integration;

import com.example.djlogportfoliobackend.dto.ProjectRequest;
import com.example.djlogportfoliobackend.entity.Project;
import com.example.djlogportfoliobackend.entity.ProjectStatus;
import com.example.djlogportfoliobackend.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ProjectController 통합 테스트")
class ProjectControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Project testProject;

    @BeforeEach
    void setUp() {
        projectRepository.deleteAll();

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
        testProject = projectRepository.save(testProject);
    }

    @Test
    @DisplayName("공개 프로젝트 목록 조회 - 비인증 사용자")
    void getProjects_PublishedOnly_Unauthenticated() throws Exception {
        mockMvc.perform(get("/api/projects")
                        .param("status", "published"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("Test Project")))
                .andExpect(jsonPath("$[0].status", is("PUBLISHED")));
    }

    @Test
    @DisplayName("모든 프로젝트 목록 조회 시도 - 비인증 사용자는 공개 프로젝트만 조회")
    void getProjects_AllRequested_UnauthenticatedGetsPublishedOnly() throws Exception {
        // Draft 프로젝트 추가
        Project draftProject = new Project(
                "Draft Project",
                "Mobile",
                ProjectStatus.DRAFT,
                "Draft Description",
                "draft-image.jpg",
                "Kotlin,Android",
                "2023-04-01 ~ 2023-06-01",
                "Draft Company",
                2
        );
        projectRepository.save(draftProject);

        mockMvc.perform(get("/api/projects")
                        .param("status", "all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1))) // DRAFT는 포함되지 않음
                .andExpect(jsonPath("$[0].status", is("PUBLISHED")));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("모든 프로젝트 목록 조회 - 인증된 사용자")
    void getProjects_All_Authenticated() throws Exception {
        // Draft 프로젝트 추가
        Project draftProject = new Project(
                "Draft Project",
                "Mobile",
                ProjectStatus.DRAFT,
                "Draft Description",
                "draft-image.jpg",
                "Kotlin,Android",
                "2023-04-01 ~ 2023-06-01",
                "Draft Company",
                2
        );
        projectRepository.save(draftProject);

        mockMvc.perform(get("/api/projects")
                        .param("status", "all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2))) // PUBLISHED + DRAFT
                .andExpect(jsonPath("$[*].status", containsInAnyOrder("PUBLISHED", "DRAFT")));
    }

    @Test
    @DisplayName("프로젝트 단건 조회 - 성공")
    void getProjectById_Success() throws Exception {
        mockMvc.perform(get("/api/projects/{id}", testProject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testProject.getId().toString())))
                .andExpect(jsonPath("$.title", is("Test Project")))
                .andExpect(jsonPath("$.category", is("Web")));
    }

    @Test
    @DisplayName("프로젝트 단건 조회 - 존재하지 않는 ID")
    void getProjectById_NotFound() throws Exception {
        UUID nonExistentId = UUID.randomUUID();

        mockMvc.perform(get("/api/projects/{id}", nonExistentId))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("프로젝트 생성 - 성공")
    void createProject_Success() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setTitle("New Project");
        request.setCategory("Mobile");
        request.setStatus(ProjectStatus.DRAFT);
        request.setDescription("New Description");
        request.setImage("new-image.jpg");
        request.setTags("Kotlin,Android");
        request.setDuration("2023-04-01 ~ 2023-06-01");
        request.setCompany("New Company");
        request.setOrder(3);

        mockMvc.perform(post("/api/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Project")))
                .andExpect(jsonPath("$.category", is("Mobile")))
                .andExpect(jsonPath("$.status", is("DRAFT")));
    }

    @Test
    @DisplayName("프로젝트 생성 - 비인증 사용자")
    void createProject_Unauthorized() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setTitle("New Project");

        mockMvc.perform(post("/api/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("프로젝트 업데이트 - 성공")
    void updateProject_Success() throws Exception {
        ProjectRequest request = new ProjectRequest();
        request.setTitle("Updated Project");
        request.setCategory("Updated Category");
        request.setStatus(ProjectStatus.PUBLISHED);
        request.setDescription("Updated Description");
        request.setImage("updated-image.jpg");
        request.setTags("Updated,Tags");
        request.setDuration("2023-07-01 ~ 2023-09-01");
        request.setCompany("Updated Company");
        request.setOrder(5);

        mockMvc.perform(put("/api/projects/{id}", testProject.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Project")))
                .andExpect(jsonPath("$.category", is("Updated Category")));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("프로젝트 삭제 - 성공")
    void deleteProject_Success() throws Exception {
        mockMvc.perform(delete("/api/projects/{id}", testProject.getId())
                        .with(csrf()))
                .andExpect(status().isNoContent());

        // 삭제 확인
        mockMvc.perform(get("/api/projects/{id}", testProject.getId()))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("카테고리별 프로젝트 조회 - 성공")
    void getProjectsByCategory_Success() throws Exception {
        mockMvc.perform(get("/api/projects")
                        .param("category", "Web"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].category", is("Web")));
    }

    @Test
    @DisplayName("태그별 프로젝트 조회 - 성공")
    void getProjectsByTag_Success() throws Exception {
        mockMvc.perform(get("/api/projects")
                        .param("tag", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].tags", containsString("Java")));
    }

    @Test
    @DisplayName("잘못된 요청 파라미터 - 유효성 검증 실패")
    void createProject_ValidationFailure() throws Exception {
        ProjectRequest invalidRequest = new ProjectRequest();
        // title이 비어있어서 유효성 검증 실패

        mockMvc.perform(post("/api/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }
}