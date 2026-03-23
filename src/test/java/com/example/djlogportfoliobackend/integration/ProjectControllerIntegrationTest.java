package com.example.djlogportfoliobackend.integration;

import com.example.djlogportfoliobackend.dto.ProjectQnAOrderBulkUpdateRequest;
import com.example.djlogportfoliobackend.dto.ProjectQnAOrderUpdateRequest;
import com.example.djlogportfoliobackend.dto.ProjectQnARequest;
import com.example.djlogportfoliobackend.dto.ProjectRequest;
import com.example.djlogportfoliobackend.entity.Project;
import com.example.djlogportfoliobackend.entity.ProjectQnA;
import com.example.djlogportfoliobackend.entity.ProjectStatus;
import com.example.djlogportfoliobackend.repository.ProjectRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.mock.web.MockMultipartFile;

import java.util.Arrays;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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
                Arrays.asList("Java", "Spring"),
                "2023-01-01 ~ 2023-03-01",
                "Test Company",
                1
        );

        ProjectQnA secondQna = new ProjectQnA();
        secondQna.setQuestion("두 번째 질문");
        secondQna.setAnswer("두 번째 답변");
        secondQna.setDisplayOrder(1);
        secondQna.setProject(testProject);

        ProjectQnA firstQna = new ProjectQnA();
        firstQna.setQuestion("첫 번째 질문");
        firstQna.setAnswer("첫 번째 답변");
        firstQna.setDisplayOrder(0);
        firstQna.setProject(testProject);

        testProject.getQnaList().add(secondQna);
        testProject.getQnaList().add(firstQna);
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
    @DisplayName("프로젝트 목록 기본 조회 - 비인증 사용자는 published만 조회")
    void getProjects_Default_UnauthenticatedGetsPublishedOnly() throws Exception {
        Project draftProject = new Project(
                "Draft Project",
                "Mobile",
                ProjectStatus.DRAFT,
                "Draft Description",
                "draft-image.jpg",
                Arrays.asList("Kotlin", "Android"),
                "2023-04-01 ~ 2023-06-01",
                "Draft Company",
                2
        );
        projectRepository.save(draftProject);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
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
                Arrays.asList("Kotlin", "Android"),
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
                Arrays.asList("Kotlin", "Android"),
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
    @WithMockUser(username = "admin@example.com")
    @DisplayName("프로젝트 목록 기본 조회 - 인증된 사용자는 전체 조회")
    void getProjects_Default_AuthenticatedGetsAll() throws Exception {
        Project draftProject = new Project(
                "Draft Project",
                "Mobile",
                ProjectStatus.DRAFT,
                "Draft Description",
                "draft-image.jpg",
                Arrays.asList("Kotlin", "Android"),
                "2023-04-01 ~ 2023-06-01",
                "Draft Company",
                2
        );
        projectRepository.save(draftProject);

        mockMvc.perform(get("/api/projects"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].status", containsInAnyOrder("PUBLISHED", "DRAFT")));
    }

    @Test
    @DisplayName("프로젝트 단건 조회 - 성공")
    void getProjectById_Success() throws Exception {
        mockMvc.perform(get("/api/projects/{id}", testProject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(testProject.getId().toString())))
                .andExpect(jsonPath("$.title", is("Test Project")))
                .andExpect(jsonPath("$.category", is("Web")))
                .andExpect(jsonPath("$.qna[0].question", is("첫 번째 질문")))
                .andExpect(jsonPath("$.qna[0].displayOrder", is(0)))
                .andExpect(jsonPath("$.qna[1].question", is("두 번째 질문")))
                .andExpect(jsonPath("$.qna[1].displayOrder", is(1)));
    }

    @Test
    @DisplayName("프로젝트 Q&A 목록 조회 - displayOrder 순 정렬")
    void getProjectQnAs_Success() throws Exception {
        mockMvc.perform(get("/api/projects/{projectId}/qna", testProject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].question", is("첫 번째 질문")))
                .andExpect(jsonPath("$[0].displayOrder", is(0)))
                .andExpect(jsonPath("$[1].question", is("두 번째 질문")))
                .andExpect(jsonPath("$[1].displayOrder", is(1)));
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
        String request = """
                {
                  "title": "New Project",
                  "category": "Mobile",
                  "status": "DRAFT",
                  "description": "New Description",
                  "image": "new-image.jpg",
                  "tags": ["Kotlin", "Android"],
                  "duration": "2023-04-01 ~ 2023-06-01",
                  "company": "New Company",
                  "order": 3,
                  "qna": [
                    { "question": "두 번째 질문", "answer": "두 번째 답변", "displayOrder": 1 },
                    { "question": "첫 번째 질문", "answer": "첫 번째 답변", "displayOrder": 0 }
                  ]
                }
                """;

        mockMvc.perform(post("/api/projects")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title", is("New Project")))
                .andExpect(jsonPath("$.category", is("Mobile")))
                .andExpect(jsonPath("$.status", is("DRAFT")))
                .andExpect(jsonPath("$.qna[0].question", is("첫 번째 질문")))
                .andExpect(jsonPath("$.qna[0].displayOrder", is(0)))
                .andExpect(jsonPath("$.qna[1].question", is("두 번째 질문")))
                .andExpect(jsonPath("$.qna[1].displayOrder", is(1)));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("프로젝트 이미지 업로드 - 성공")
    void uploadProjectImage_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "project.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                new byte[]{
                        (byte) 0xFF, (byte) 0xD8, (byte) 0xFF, 0x00,
                        0x00, 0x00, 0x00, 0x00,
                        0x00, 0x00, 0x00, 0x00
                }
        );

        mockMvc.perform(multipart("/api/projects/image")
                        .file(file)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.image", startsWith("/uploads/")))
                .andExpect(jsonPath("$.url", startsWith("/uploads/")))
                .andExpect(jsonPath("$.path", startsWith("/uploads/")));
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
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("프로젝트 업데이트 - 성공")
    void updateProject_Success() throws Exception {
        String request = """
                {
                  "title": "Updated Project",
                  "category": "Updated Category",
                  "status": "PUBLISHED",
                  "description": "Updated Description",
                  "image": "updated-image.jpg",
                  "tags": ["Updated", "Tags"],
                  "duration": "2023-07-01 ~ 2023-09-01",
                  "company": "Updated Company",
                  "order": 5,
                  "qna": [
                    { "question": "업데이트 질문2", "answer": "업데이트 답변2", "displayOrder": 1 },
                    { "question": "업데이트 질문1", "answer": "업데이트 답변1", "displayOrder": 0 }
                  ]
                }
                """;

        mockMvc.perform(put("/api/projects/{id}", testProject.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title", is("Updated Project")))
                .andExpect(jsonPath("$.category", is("Updated Category")))
                .andExpect(jsonPath("$.qna[0].question", is("업데이트 질문1")))
                .andExpect(jsonPath("$.qna[0].displayOrder", is(0)))
                .andExpect(jsonPath("$.qna[1].question", is("업데이트 질문2")))
                .andExpect(jsonPath("$.qna[1].displayOrder", is(1)));

        mockMvc.perform(get("/api/projects/{id}", testProject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qna[0].question", is("업데이트 질문1")))
                .andExpect(jsonPath("$.qna[0].displayOrder", is(0)))
                .andExpect(jsonPath("$.qna[1].question", is("업데이트 질문2")))
                .andExpect(jsonPath("$.qna[1].displayOrder", is(1)));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("프로젝트 업데이트 - qna 누락 시 기존 Q&A 유지")
    void updateProject_WithoutQnaPayload_KeepsExistingQna() throws Exception {
        String request = """
                {
                  "title": "Updated Project",
                  "category": "Updated Category",
                  "status": "PUBLISHED",
                  "description": "Updated Description",
                  "image": "updated-image.jpg",
                  "tags": ["Updated", "Tags"],
                  "duration": "2023-07-01 ~ 2023-09-01",
                  "company": "Updated Company",
                  "order": 5
                }
                """;

        mockMvc.perform(put("/api/projects/{id}", testProject.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qna[0].question", is("첫 번째 질문")))
                .andExpect(jsonPath("$.qna[1].question", is("두 번째 질문")));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("프로젝트 업데이트 - qna 빈 배열이면 기존 Q&A 삭제")
    void updateProject_WithEmptyQna_DeletesExistingQna() throws Exception {
        String request = """
                {
                  "title": "Updated Project",
                  "category": "Updated Category",
                  "status": "PUBLISHED",
                  "description": "Updated Description",
                  "image": "updated-image.jpg",
                  "tags": ["Updated", "Tags"],
                  "duration": "2023-07-01 ~ 2023-09-01",
                  "company": "Updated Company",
                  "order": 5,
                  "qna": []
                }
                """;

        mockMvc.perform(put("/api/projects/{id}", testProject.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qna", hasSize(0)));

        mockMvc.perform(get("/api/projects/{id}", testProject.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.qna", hasSize(0)));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("프로젝트 Q&A 순서 일괄 변경 - 성공")
    void updateProjectQnADisplayOrders_Success() throws Exception {
        ProjectQnAOrderUpdateRequest firstUpdate = new ProjectQnAOrderUpdateRequest();
        firstUpdate.setId(testProject.getQnaList().get(0).getId());
        firstUpdate.setDisplayOrder(1);

        ProjectQnAOrderUpdateRequest secondUpdate = new ProjectQnAOrderUpdateRequest();
        secondUpdate.setId(testProject.getQnaList().get(1).getId());
        secondUpdate.setDisplayOrder(0);

        ProjectQnAOrderBulkUpdateRequest request = new ProjectQnAOrderBulkUpdateRequest();
        request.setQnaOrders(Arrays.asList(firstUpdate, secondUpdate));

        mockMvc.perform(put("/api/projects/{projectId}/qna/display-order", testProject.getId())
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(testProject.getQnaList().get(1).getId().toString())))
                .andExpect(jsonPath("$[0].displayOrder", is(0)))
                .andExpect(jsonPath("$[1].id", is(testProject.getQnaList().get(0).getId().toString())))
                .andExpect(jsonPath("$[1].displayOrder", is(1)));
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
                .andExpect(jsonPath("$[0].tags", hasItem("Java")));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
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
