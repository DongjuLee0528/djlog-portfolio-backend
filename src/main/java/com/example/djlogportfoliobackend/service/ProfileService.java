package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.ProfileRequest;
import com.example.djlogportfoliobackend.dto.ProfileResponse;
import com.example.djlogportfoliobackend.dto.SkillRequest;
import com.example.djlogportfoliobackend.dto.SkillResponse;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.entity.Skill;
import com.example.djlogportfoliobackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProfileService {

    private final ProfileRepository profileRepository;

    public ProfileResponse getProfile() {
        Profile profile = profileRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("프로필을 찾을 수 없습니다."));
        return convertToResponse(profile);
    }

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

            // Clear existing skills and add new ones
            profile.getSkills().clear();
            if (request.getSkills() != null) {
                for (SkillRequest skillRequest : request.getSkills()) {
                    Skill skill = new Skill(skillRequest.getName(), skillRequest.getCategory(),
                                          skillRequest.getProficiency(), profile);
                    profile.getSkills().add(skill);
                }
            }
        }

        Profile savedProfile = profileRepository.save(profile);
        return convertToResponse(savedProfile);
    }

    @Transactional
    public ProfileResponse createProfile(ProfileRequest request) {
        Profile profile = convertToEntity(request);
        Profile savedProfile = profileRepository.save(profile);
        return convertToResponse(savedProfile);
    }

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

        // Add skills
        if (request.getSkills() != null) {
            for (SkillRequest skillRequest : request.getSkills()) {
                Skill skill = new Skill(skillRequest.getName(), skillRequest.getCategory(),
                                      skillRequest.getProficiency(), profile);
                profile.getSkills().add(skill);
            }
        }

        return profile;
    }

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

        return response;
    }

    /**
     * Skill 엔티티를 SkillResponse DTO로 변환
     *
     * @param skill Skill 엔티티
     * @return SkillResponse DTO
     */
    private SkillResponse convertToSkillResponse(Skill skill) {
        SkillResponse response = new SkillResponse();
        response.setId(skill.getId());
        response.setName(skill.getName());
        response.setCategory(skill.getCategory());
        response.setProficiency(skill.getProficiency());
        return response;
    }
}