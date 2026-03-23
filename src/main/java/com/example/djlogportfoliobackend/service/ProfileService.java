package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.AchievementRequest;
import com.example.djlogportfoliobackend.dto.AchievementResponse;
import com.example.djlogportfoliobackend.dto.CertificateRequest;
import com.example.djlogportfoliobackend.dto.CertificateResponse;
import com.example.djlogportfoliobackend.dto.EducationRequest;
import com.example.djlogportfoliobackend.dto.EducationResponse;
import com.example.djlogportfoliobackend.dto.ProfileRequest;
import com.example.djlogportfoliobackend.dto.ProfileResponse;
import com.example.djlogportfoliobackend.dto.SkillRequest;
import com.example.djlogportfoliobackend.dto.SkillResponse;
import com.example.djlogportfoliobackend.entity.Achievement;
import com.example.djlogportfoliobackend.entity.Certificate;
import com.example.djlogportfoliobackend.entity.Education;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.entity.Skill;
import com.example.djlogportfoliobackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 프로필 관리 서비스
 *
 * 사용자의 개인 프로필 정보를 관리한다.
 * 프로필은 시스템에 단 하나만 존재하며, 기술 스택 정보도 함께 관리한다.
 *
 * 주요 기능:
 * - 프로필 정보 조회
 * - 프로필 정보 수정 (기술 스택 포함)
 * - 프로필 생성 (최초 설정 시)
 * - 엔티티와 DTO 간 변환
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private static final Pattern YEAR_PATTERN = Pattern.compile("(19|20)\\d{2}");

    private final ProfileRepository profileRepository;

    /**
     * 현재 프로필 조회
     * 시스템에 저장된 단일 프로필을 조회합니다.
     *
     * @return 프로필 응답 DTO
     * @throws RuntimeException 프로필을 찾을 수 없는 경우
     */
    public ProfileResponse getProfile() {
        Profile profile = profileRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("프로필을 찾을 수 없습니다."));
        return convertToResponse(profile);
    }

    /**
     * 프로필 정보 수정
     * 기존 프로필이 있으면 수정하고, 없으면 새로 생성합니다.
     * 기술 스택 정보는 기존 데이터를 모두 삭제하고 새로 추가합니다.
     *
     * @param request 프로필 수정 요청 DTO
     * @return 수정된 프로필 응답 DTO
     */
    @Transactional
    public ProfileResponse updateProfile(ProfileRequest request) {
        Profile profile = profileRepository.findAll().stream()
                .findFirst()
                .orElse(null);

        if (profile == null) {
            profile = convertToEntity(request);
        } else {
            profile.setName(request.getName());
            profile.setJob(request.getJob());
            profile.setBio(request.getBio());
            profile.setAbout(request.getAbout());
            profile.setImage(request.getImage());
            profile.setEmail(request.getEmail());
            profile.setGithub(request.getGithub());
            profile.setResume(request.getResume());
        }

        replaceSkills(profile, request);
        replaceEducations(profile, request);
        replaceCertificates(profile, request);
        replaceAchievements(profile, request);

        Profile savedProfile = profileRepository.save(profile);
        return convertToResponse(savedProfile);
    }

    /**
     * 새 프로필 생성
     * 새로운 프로필을 생성하고 데이터베이스에 저장합니다.
     *
     * @param request 프로필 생성 요청 DTO
     * @return 생성된 프로필 응답 DTO
     */
    @Transactional
    public ProfileResponse createProfile(ProfileRequest request) {
        Profile profile = convertToEntity(request);
        Profile savedProfile = profileRepository.save(profile);
        return convertToResponse(savedProfile);
    }

    /**
     * 프로필 요청 DTO를 엔티티로 변환
     * 요청 DTO의 정보를 바탕으로 Profile 엔티티를 생성하고 기술 스택도 함께 설정합니다.
     *
     * @param request 프로필 요청 DTO
     * @return Profile 엔티티
     */
    private Profile convertToEntity(ProfileRequest request) {
        Profile profile = new Profile(
                request.getName(),
                request.getBio(),
                request.getAbout(),
                request.getImage(),
                request.getEmail(),
                request.getGithub()
        );

        profile.setJob(request.getJob());
        profile.setResume(request.getResume());

        replaceSkills(profile, request);
        replaceEducations(profile, request);
        replaceCertificates(profile, request);
        replaceAchievements(profile, request);

        return profile;
    }

    /**
     * 프로필 엔티티를 응답 DTO로 변환
     * Profile 엔티티의 정보를 바탕으로 응답 DTO를 생성하고 기술 스택도 함께 변환합니다.
     *
     * @param profile Profile 엔티티
     * @return ProfileResponse DTO
     */
    private ProfileResponse convertToResponse(Profile profile) {
        ProfileResponse response = new ProfileResponse();
        response.setId(profile.getId());
        response.setName(profile.getName());
        response.setJob(profile.getJob());
        response.setBio(profile.getBio());
        response.setAbout(profile.getAbout());
        response.setImage(profile.getImage());
        response.setEmail(profile.getEmail());
        response.setGithub(profile.getGithub());
        response.setResume(profile.getResume());

        // Convert skills to DTOs
        if (profile.getSkills() != null) {
            response.setSkills(profile.getSkills().stream()
                    .map(this::convertToSkillResponse)
                    .collect(Collectors.toList()));
        }

        if (profile.getEducations() != null) {
            response.setEducation(profile.getEducations().stream()
                    .map(this::convertToEducationResponse)
                    .collect(Collectors.toList()));
        }

        if (profile.getCertificates() != null) {
            List<CertificateResponse> certificates = profile.getCertificates().stream()
                    .sorted(Comparator.comparing(
                            Certificate::getIssueDate,
                            Comparator.nullsLast(Comparator.reverseOrder())
                    ))
                    .map(this::convertToCertificateResponse)
                    .toList();
            response.setCertificates(certificates);
        }

        if (profile.getAchievements() != null) {
            List<AchievementResponse> achievements = profile.getAchievements().stream()
                    .sorted(Comparator.comparing(
                            achievement -> extractLatestYear(achievement.getPeriod()),
                            Comparator.nullsLast(Comparator.reverseOrder())
                    ))
                    .map(this::convertToAchievementResponse)
                    .toList();
            response.setAchievements(achievements);
        }

        return response;
    }

    private void replaceSkills(Profile profile, ProfileRequest request) {
        profile.getSkills().clear();
        if (request.getSkills() == null) {
            return;
        }

        for (SkillRequest skillRequest : request.getSkills()) {
            Skill skill = new Skill(
                    skillRequest.getName(),
                    skillRequest.getCategory(),
                    skillRequest.getProficiency(),
                    profile
            );
            profile.getSkills().add(skill);
        }
    }

    private void replaceEducations(Profile profile, ProfileRequest request) {
        profile.getEducations().clear();
        if (request.getEducation() == null) {
            return;
        }

        for (EducationRequest educationRequest : request.getEducation()) {
            Education education = new Education(
                    educationRequest.getSchool(),
                    educationRequest.getMajor(),
                    educationRequest.getPeriod(),
                    educationRequest.getDegree(),
                    profile
            );
            profile.getEducations().add(education);
        }
    }

    private void replaceCertificates(Profile profile, ProfileRequest request) {
        profile.getCertificates().clear();
        if (request.getCertificates() == null) {
            return;
        }

        for (CertificateRequest certificateRequest : request.getCertificates()) {
            Certificate certificate = new Certificate(
                    certificateRequest.getName(),
                    certificateRequest.getIssuer(),
                    certificateRequest.parseIssueDate(),
                    certificateRequest.getCredentialId(),
                    profile
            );
            profile.getCertificates().add(certificate);
        }
    }

    private void replaceAchievements(Profile profile, ProfileRequest request) {
        profile.getAchievements().clear();
        if (request.getAchievements() == null) {
            return;
        }

        for (AchievementRequest achievementRequest : request.getAchievements()) {
            Achievement achievement = new Achievement(
                    achievementRequest.getTitle(),
                    achievementRequest.getOrganization(),
                    achievementRequest.getDescription(),
                    achievementRequest.getPeriod(),
                    achievementRequest.getCategory(),
                    profile
            );
            profile.getAchievements().add(achievement);
        }
    }

    /**
     * Skill 엔티티를 SkillResponse DTO로 변환
     *
     * @param skill Skill 엔티티
     * @return SkillResponse DTO
     */
    private SkillResponse convertToSkillResponse(Skill skill) {
        return new SkillResponse(skill);
    }

    private EducationResponse convertToEducationResponse(Education education) {
        return new EducationResponse(education);
    }

    private CertificateResponse convertToCertificateResponse(Certificate certificate) {
        return new CertificateResponse(certificate);
    }

    private AchievementResponse convertToAchievementResponse(Achievement achievement) {
        return new AchievementResponse(achievement);
    }

    private Integer extractLatestYear(String period) {
        if (period == null || period.isBlank()) {
            return null;
        }

        Matcher matcher = YEAR_PATTERN.matcher(period);
        Integer latestYear = null;
        while (matcher.find()) {
            int year = Integer.parseInt(matcher.group());
            if (latestYear == null || year > latestYear) {
                latestYear = year;
            }
        }

        return latestYear;
    }
}
