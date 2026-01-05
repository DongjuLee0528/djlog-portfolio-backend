package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.ProfileRequest;
import com.example.djlogportfoliobackend.dto.ProfileResponse;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
            profile.setBio(request.getBio());
            profile.setAbout(request.getAbout());
            profile.setImage(request.getImage());
            profile.setEmail(request.getEmail());
            profile.setGithub(request.getGithub());
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
        return new Profile(
                request.getName(),
                request.getBio(),
                request.getAbout(),
                request.getImage(),
                request.getEmail(),
                request.getGithub()
        );
    }

    private ProfileResponse convertToResponse(Profile profile) {
        ProfileResponse response = new ProfileResponse();
        response.setId(profile.getId());
        response.setName(profile.getName());
        response.setBio(profile.getBio());
        response.setAbout(profile.getAbout());
        response.setImage(profile.getImage());
        response.setEmail(profile.getEmail());
        response.setGithub(profile.getGithub());
        return response;
    }
}