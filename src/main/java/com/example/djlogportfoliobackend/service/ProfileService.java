package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.ProfileRequest;
import com.example.djlogportfoliobackend.dto.ProfileResponse;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.repository.ProfileRepository;
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
public class ProfileService {

    private final ProfileRepository profileRepository;

    public List<ProfileResponse> getAllProfiles() {
        return profileRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public Optional<ProfileResponse> getProfileById(UUID id) {
        return profileRepository.findById(id)
                .map(this::convertToResponse);
    }

    @Transactional
    public ProfileResponse createProfile(ProfileRequest request) {
        Profile profile = convertToEntity(request);
        Profile savedProfile = profileRepository.save(profile);
        return convertToResponse(savedProfile);
    }

    @Transactional
    public ProfileResponse updateProfile(UUID id, ProfileRequest request) {
        return profileRepository.findById(id)
                .map(profile -> {
                    profile.setName(request.getName());
                    profile.setBio(request.getBio());
                    profile.setAbout(request.getAbout());
                    profile.setImage(request.getImage());
                    profile.setEmail(request.getEmail());
                    profile.setGithub(request.getGithub());
                    Profile savedProfile = profileRepository.save(profile);
                    return convertToResponse(savedProfile);
                })
                .orElseThrow(() -> new RuntimeException("Profile not found with id: " + id));
    }

    @Transactional
    public void deleteProfile(UUID id) {
        profileRepository.deleteById(id);
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