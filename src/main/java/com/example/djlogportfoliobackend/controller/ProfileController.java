package com.example.djlogportfoliobackend.controller;

import com.example.djlogportfoliobackend.dto.ProfileRequest;
import com.example.djlogportfoliobackend.dto.ProfileResponse;
import com.example.djlogportfoliobackend.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<List<ProfileResponse>> getAllProfiles() {
        List<ProfileResponse> profiles = profileService.getAllProfiles();
        return ResponseEntity.ok(profiles);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileResponse> getProfileById(@PathVariable UUID id) {
        return profileService.getProfileById(id)
                .map(profile -> ResponseEntity.ok(profile))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ProfileResponse> createProfile(@Valid @RequestBody ProfileRequest request) {
        ProfileResponse createdProfile = profileService.createProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdProfile);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfileResponse> updateProfile(@PathVariable UUID id, @Valid @RequestBody ProfileRequest request) {
        try {
            ProfileResponse updatedProfile = profileService.updateProfile(id, request);
            return ResponseEntity.ok(updatedProfile);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfile(@PathVariable UUID id) {
        profileService.deleteProfile(id);
        return ResponseEntity.noContent().build();
    }
}