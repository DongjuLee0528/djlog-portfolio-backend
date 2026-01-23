package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * 프로필 정보를 담는 응답 DTO
 */
@Data
public class ProfileResponse {

    /**
     * 프로필 고유 식별자
     */
    private UUID id;

    /**
     * 이름
     */
    private String name;

    /**
     * 직업/직무
     * 예: Backend Developer, Full Stack Developer
     */
    private String job;

    /**
     * 간단한 자기소개
     * 한 줄 정도의 짧은 소개
     */
    private String bio;

    /**
     * 상세한 자기소개
     * 경력, 관심사, 목표 등을 포함한 긴 소개
     */
    private String about;

    /**
     * 프로필 이미지 URL
     */
    private String image;

    /**
     * 이메일 주소
     */
    private String email;

    /**
     * GitHub 프로필 URL
     */
    private String github;

    /**
     * 이력서/CV URL
     */
    private String resume;

    /**
     * 보유 기술 스택 목록
     */
    private List<SkillResponse> skills;
}