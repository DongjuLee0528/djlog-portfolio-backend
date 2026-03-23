package com.example.djlogportfoliobackend.integration;

import com.example.djlogportfoliobackend.dto.AchievementRequest;
import com.example.djlogportfoliobackend.dto.CertificateRequest;
import com.example.djlogportfoliobackend.dto.EducationRequest;
import com.example.djlogportfoliobackend.dto.ProfileRequest;
import com.example.djlogportfoliobackend.dto.SkillRequest;
import com.example.djlogportfoliobackend.entity.Achievement;
import com.example.djlogportfoliobackend.entity.Certificate;
import com.example.djlogportfoliobackend.entity.Education;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.entity.Skill;
import com.example.djlogportfoliobackend.repository.AchievementRepository;
import com.example.djlogportfoliobackend.repository.CertificateRepository;
import com.example.djlogportfoliobackend.repository.EducationRepository;
import com.example.djlogportfoliobackend.repository.ProfileRepository;
import com.example.djlogportfoliobackend.repository.SkillRepository;
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

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@DisplayName("ProfileController 통합 테스트")
class ProfileControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private SkillRepository skillRepository;

    @Autowired
    private EducationRepository educationRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private AchievementRepository achievementRepository;

    @BeforeEach
    void setUp() {
        achievementRepository.deleteAll();
        certificateRepository.deleteAll();
        educationRepository.deleteAll();
        skillRepository.deleteAll();
        profileRepository.deleteAll();
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("PUT /api/profile 는 skills, education, certificates, achievements 를 함께 저장하고 응답에 포함한다")
    void updateProfile_SavesWholePayloadAndReturnsChildren() throws Exception {
        ProfileRequest request = buildProfileRequest(
                List.of(buildSkill("Java", "Backend", "Advanced"), buildSkill("React", "Frontend", "Intermediate")),
                List.of(buildEducation("A대학교", "컴퓨터공학", "2018.03 - 2022.02", "학사")),
                List.of(buildCertificate("정보처리기사", "한국산업인력공단", LocalDate.of(2022, 6, 10), "CERT-001")),
                List.of(buildAchievement("사내 멘토", "Example Corp", "주니어 개발자 멘토링", "2023", "Mentoring"))
        );

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Dongju Lee")))
                .andExpect(jsonPath("$.skills", hasSize(2)))
                .andExpect(jsonPath("$.education", hasSize(1)))
                .andExpect(jsonPath("$.education[0].school", is("A대학교")))
                .andExpect(jsonPath("$.certificates", hasSize(1)))
                .andExpect(jsonPath("$.certificates[0].name", is("정보처리기사")))
                .andExpect(jsonPath("$.achievements", hasSize(1)))
                .andExpect(jsonPath("$.achievements[0].title", is("사내 멘토")));

        Profile savedProfile = profileRepository.findAll().stream().findFirst().orElseThrow();
        assertEquals("Dongju Lee", savedProfile.getName());
        assertEquals(2, skillRepository.count());
        assertEquals(1, educationRepository.count());
        assertEquals(1, certificateRepository.count());
        assertEquals(1, achievementRepository.count());
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("PUT /api/profile 는 achievements 를 포함한 하위 컬렉션을 전체 교체하고 요청에 없는 기존 항목을 삭제한다")
    void updateProfile_ReplacesExistingCollectionsAndDeletesRemovedItems() throws Exception {
        Profile profile = new Profile("Old Name", "old bio", "old about", "old.png", "old@test.com", "old-github");
        profile.setJob("Old Job");
        profile.setResume("old-resume.pdf");
        profile.getSkills().add(new Skill("Old Skill", "Backend", "Beginner", profile));
        profile.getEducations().add(new Education("Old School", "Old Major", "2010.03 - 2014.02", "학사", profile));
        profile.getCertificates().add(new Certificate("Old Certificate", "Old Issuer", LocalDate.of(2020, 1, 1), "OLD-1", profile));
        profile.getAchievements().add(new Achievement("Old Award", "Old Org", "Old Desc", "2020", "Awards", profile));
        profileRepository.save(profile);

        ProfileRequest request = buildProfileRequest(
                List.of(buildSkill("Spring Boot", "Backend", "Expert")),
                List.of(buildEducation("New School", "New Major", "2015.03 - 2019.02", "학사")),
                List.of(buildCertificate("SQLD", "한국데이터산업진흥원", LocalDate.of(2023, 11, 18), "NEW-1")),
                List.of(buildAchievement("테크 리드", "New Org", "프로젝트 리딩", "2024", "Leadership"))
        );

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills", hasSize(1)))
                .andExpect(jsonPath("$.skills[0].name", is("Spring Boot")))
                .andExpect(jsonPath("$.education", hasSize(1)))
                .andExpect(jsonPath("$.education[0].school", is("New School")))
                .andExpect(jsonPath("$.certificates", hasSize(1)))
                .andExpect(jsonPath("$.certificates[0].name", is("SQLD")))
                .andExpect(jsonPath("$.achievements", hasSize(1)))
                .andExpect(jsonPath("$.achievements[0].title", is("테크 리드")));

        assertEquals(1, skillRepository.count());
        assertEquals(1, educationRepository.count());
        assertEquals(1, certificateRepository.count());
        assertEquals(1, achievementRepository.count());

        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.skills[0].name", is("Spring Boot")))
                .andExpect(jsonPath("$.education[0].school", is("New School")))
                .andExpect(jsonPath("$.certificates[0].name", is("SQLD")))
                .andExpect(jsonPath("$.achievements[0].title", is("테크 리드")));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("PUT /api/profile 는 yyyy.MM, yyyy-MM, yyyy-MM-dd 형식의 certificates.issueDate 를 LocalDate 로 변환해 저장한다")
    void updateProfile_ParsesCertificateIssueDateCompatibleFormats() throws Exception {
        String requestBody = """
                {
                  "name": "Dongju Lee",
                  "certificates": [
                    {
                      "name": "Dot Format",
                      "issuer": "Org",
                      "issueDate": "2025.05",
                      "credentialId": "CERT-001"
                    },
                    {
                      "name": "Dash Format",
                      "issuer": "Org",
                      "issueDate": "2025-04",
                      "credentialId": "CERT-002"
                    },
                    {
                      "name": "Exact Date",
                      "issuer": "Org",
                      "issueDate": "2025-03-15",
                      "credentialId": "CERT-003"
                    }
                  ]
                }
                """;

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificates", hasSize(3)))
                .andExpect(jsonPath("$.certificates[0].name", is("Dot Format")))
                .andExpect(jsonPath("$.certificates[0].issueDate", is("2025-05-01")))
                .andExpect(jsonPath("$.certificates[1].name", is("Dash Format")))
                .andExpect(jsonPath("$.certificates[1].issueDate", is("2025-04-01")))
                .andExpect(jsonPath("$.certificates[2].name", is("Exact Date")))
                .andExpect(jsonPath("$.certificates[2].issueDate", is("2025-03-15")));

        Profile savedProfile = profileRepository.findAll().stream().findFirst().orElseThrow();
        assertEquals(
                LocalDate.of(2025, 5, 1),
                savedProfile.getCertificates().stream()
                        .filter(certificate -> certificate.getName().equals("Dot Format"))
                        .findFirst()
                        .orElseThrow()
                        .getIssueDate()
        );
        assertEquals(
                LocalDate.of(2025, 4, 1),
                savedProfile.getCertificates().stream()
                        .filter(certificate -> certificate.getName().equals("Dash Format"))
                        .findFirst()
                        .orElseThrow()
                        .getIssueDate()
        );
        assertEquals(
                LocalDate.of(2025, 3, 15),
                savedProfile.getCertificates().stream()
                        .filter(certificate -> certificate.getName().equals("Exact Date"))
                        .findFirst()
                        .orElseThrow()
                        .getIssueDate()
        );
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("GET /api/profile 응답의 certificates 는 변환된 issueDate 기준 최신순으로 정렬된다")
    void getProfile_SortsCertificatesByParsedIssueDateDesc() throws Exception {
        String requestBody = """
                {
                  "name": "Dongju Lee",
                  "certificates": [
                    {
                      "name": "Month Start",
                      "issuer": "Org",
                      "issueDate": "2025.05",
                      "credentialId": "CERT-001"
                    },
                    {
                      "name": "Latest",
                      "issuer": "Org",
                      "issueDate": "2025-05-15",
                      "credentialId": "CERT-002"
                    },
                    {
                      "name": "Earlier",
                      "issuer": "Org",
                      "issueDate": "2025-04",
                      "credentialId": "CERT-003"
                    }
                  ]
                }
                """;

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificates[0].name", is("Latest")))
                .andExpect(jsonPath("$.certificates[0].issueDate", is("2025-05-15")))
                .andExpect(jsonPath("$.certificates[1].name", is("Month Start")))
                .andExpect(jsonPath("$.certificates[1].issueDate", is("2025-05-01")))
                .andExpect(jsonPath("$.certificates[2].name", is("Earlier")))
                .andExpect(jsonPath("$.certificates[2].issueDate", is("2025-04-01")));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("GET /api/profile 응답의 certificates 는 issueDate 최신순으로 정렬된다")
    void getProfile_SortsCertificatesByIssueDateDesc() throws Exception {
        ProfileRequest request = buildProfileRequest(
                List.of(buildSkill("Java", "Backend", "Advanced")),
                List.of(buildEducation("A대학교", "컴퓨터공학", "2018.03 - 2022.02", "학사")),
                List.of(
                        buildCertificate("First", "Org", LocalDate.of(2022, 1, 1), "CERT-001"),
                        buildCertificate("Latest", "Org", LocalDate.of(2024, 1, 1), "CERT-002"),
                        buildCertificate("Middle", "Org", LocalDate.of(2023, 1, 1), "CERT-003")
                ),
                List.of(buildAchievement("사내 코치", "Example Corp", "기술 코칭", "2024", "Mentoring"))
        );

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.certificates[0].name", is("Latest")))
                .andExpect(jsonPath("$.certificates[1].name", is("Middle")))
                .andExpect(jsonPath("$.certificates[2].name", is("First")))
                .andExpect(jsonPath("$.achievements[0].title", is("사내 코치")));
    }

    @Test
    @WithMockUser(username = "admin@example.com")
    @DisplayName("GET /api/profile 응답의 achievements 는 period 기준 최신순이며 파싱 실패 또는 null 은 뒤로 간다")
    void getProfile_SortsAchievementsByPeriodDescAndInvalidLast() throws Exception {
        ProfileRequest request = buildProfileRequest(
                List.of(buildSkill("Java", "Backend", "Advanced")),
                List.of(buildEducation("A대학교", "컴퓨터공학", "2018.03 - 2022.02", "학사")),
                List.of(buildCertificate("SQLD", "Org", LocalDate.of(2024, 1, 1), "CERT-001")),
                List.of(
                        buildAchievement("오래된 리더십", "Org", "Old", "2021", "Leadership"),
                        buildAchievement("최근 멘토링", "Org", "Recent", "2022 - 2024", "Mentoring"),
                        buildAchievement("파싱 실패", "Org", "Invalid", "상시", "Awards"),
                        buildAchievement("기간 없음", "Org", "Null", null, "Awards")
                )
        );

        mockMvc.perform(put("/api/profile")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/profile"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.achievements[0].title", is("최근 멘토링")))
                .andExpect(jsonPath("$.achievements[1].title", is("오래된 리더십")))
                .andExpect(jsonPath("$.achievements[2].title", is("파싱 실패")))
                .andExpect(jsonPath("$.achievements[3].title", is("기간 없음")));
    }

    private ProfileRequest buildProfileRequest(
            List<SkillRequest> skills,
            List<EducationRequest> education,
            List<CertificateRequest> certificates,
            List<AchievementRequest> achievements
    ) {
        ProfileRequest request = new ProfileRequest();
        request.setName("Dongju Lee");
        request.setJob("Backend Developer");
        request.setBio("Bio");
        request.setAbout("About");
        request.setImage("profile.png");
        request.setEmail("dongju@example.com");
        request.setGithub("https://github.com/dongju");
        request.setResume("resume.pdf");
        request.setSkills(skills);
        request.setEducation(education);
        request.setCertificates(certificates);
        request.setAchievements(achievements);
        return request;
    }

    private SkillRequest buildSkill(String name, String category, String proficiency) {
        SkillRequest request = new SkillRequest();
        request.setName(name);
        request.setCategory(category);
        request.setProficiency(proficiency);
        return request;
    }

    private EducationRequest buildEducation(String school, String major, String period, String degree) {
        EducationRequest request = new EducationRequest();
        request.setSchool(school);
        request.setMajor(major);
        request.setPeriod(period);
        request.setDegree(degree);
        return request;
    }

    private CertificateRequest buildCertificate(String name, String issuer, LocalDate issueDate, String credentialId) {
        CertificateRequest request = new CertificateRequest();
        request.setName(name);
        request.setIssuer(issuer);
        request.setIssueDate(issueDate == null ? null : issueDate.toString());
        request.setCredentialId(credentialId);
        return request;
    }

    private AchievementRequest buildAchievement(
            String title,
            String organization,
            String description,
            String period,
            String category
    ) {
        AchievementRequest request = new AchievementRequest();
        request.setTitle(title);
        request.setOrganization(organization);
        request.setDescription(description);
        request.setPeriod(period);
        request.setCategory(category);
        return request;
    }
}
