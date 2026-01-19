package com.example.djlogportfoliobackend.controller;

import com.example.djlogportfoliobackend.dto.ProfileRequest;
import com.example.djlogportfoliobackend.dto.ProfileResponse;
import com.example.djlogportfoliobackend.service.ProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 프로필 관련 REST API 컨트롤러
 * 사용자 프로필 정보의 조회와 수정 기능을 제공합니다.
 */
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     * 현재 사용자의 프로필 정보를 조회합니다.
     *
     * @return 프로필 정보
     */
    @GetMapping
    public ResponseEntity<ProfileResponse> getProfile() {
        ProfileResponse profile = profileService.getProfile();
        return ResponseEntity.ok(profile);
    }

    /**
     * 현재 사용자의 프로필 정보를 수정합니다.
     *
     * @param request 프로필 수정 요청 정보
     * @return 수정된 프로필 정보
     */
    @PutMapping
    public ResponseEntity<ProfileResponse> updateProfile(@Valid @RequestBody ProfileRequest request) {
        ProfileResponse updatedProfile = profileService.updateProfile(request);
        return ResponseEntity.ok(updatedProfile);
    }
}