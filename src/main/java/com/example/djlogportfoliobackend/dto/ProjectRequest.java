package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.ProjectStatus;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 프로젝트 생성/수정 요청 DTO
 */
@Data
public class ProjectRequest {

    /**
     * 프로젝트 제목 (필수)
     */
    @NotBlank(message = "프로젝트 제목은 필수입니다")
    private String title;

    /**
     * 프로젝트 카테고리
     * 예: 웹 개발, 모바일 앱, API 개발 등
     */
    private String category;

    /**
     * 프로젝트 상태
     * 진행중, 완료, 중단 등
     */
    private ProjectStatus status;

    /**
     * 프로젝트 상세 설명
     */
    private String description;

    /**
     * 프로젝트 대표 이미지 URL
     */
    private String image;

    /**
     * 프로젝트 태그 목록
     * 예: ["React", "Spring Boot", "MySQL"]
     */
    private List<String> tags;

    /**
     * 프로젝트에서 사용한 기술 스택 목록
     */
    @Valid
    private List<ProjectSkillRequest> skills;

    /**
     * 프로젝트 관련 링크 목록
     */
    @Valid
    private List<ProjectLinkRequest> links;

    /**
     * 프로젝트 Q&A 목록
     */
    @Valid
    private List<ProjectQnARequest> qnaList;

    /**
     * 프로젝트 진행 기간
     * 예: "2024.01 - 2024.03", "3개월"
     */
    private String duration;

    /**
     * 참여 회사 또는 기관명
     */
    private String company;

    /**
     * 프로젝트 정렬 순서
     * 낮은 숫자일수록 우선 순위가 높음
     */
    private Integer order;
}