package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.ProjectStatus;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 정보를 담는 응답 DTO
 */
@Data
public class ProjectResponse {

    /**
     * 프로젝트 고유 식별자
     */
    private UUID id;

    /**
     * 프로젝트 제목
     */
    private String title;

    /**
     * 프로젝트 카테고리 (예: 웹 개발, 모바일 앱 등)
     */
    private String category;

    /**
     * 프로젝트 상태 (진행중, 완료 등)
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
     */
    private List<String> tags;

    /**
     * 프로젝트에서 사용한 기술 스택 목록
     */
    private List<ProjectSkillResponse> skills;

    /**
     * 프로젝트 진행 기간
     */
    private String duration;

    /**
     * 참여 회사 또는 기관명
     */
    private String company;

    /**
     * 프로젝트 정렬 순서
     */
    private Integer order;

    /**
     * 프로젝트 관련 링크 목록 (GitHub, 데모 사이트 등)
     */
    private List<ProjectLinkResponse> links;

    /**
     * 프로젝트 Q&A 목록
     */
    private List<ProjectQnAResponse> qnaList;
}