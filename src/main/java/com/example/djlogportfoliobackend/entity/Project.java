package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 포트폴리오 프로젝트 정보를 저장하는 엔티티 클래스
 * 프로젝트의 기본 정보, 기술 스택, 링크, Q&A 등을 관리
 */
@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
public class Project {

    /**
     * 프로젝트 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * 프로젝트 제목 (필수)
     */
    @Column(nullable = false)
    @NotBlank(message = "프로젝트 제목은 필수입니다")
    private String title;

    /**
     * 프로젝트 카테고리 (예: 웹, 모바일, 데스크톱)
     */
    @Column
    private String category;

    /**
     * 프로젝트 상태 (DRAFT, PUBLISHED 등)
     */
    @Enumerated(EnumType.STRING)
    @Column
    private ProjectStatus status = ProjectStatus.DRAFT;

    /**
     * 프로젝트 상세 설명
     */
    @Column(columnDefinition = "TEXT")
    private String description;

    /**
     * 프로젝트 대표 이미지 경로
     */
    @Column
    private String image;

    /**
     * 프로젝트 관련 태그 목록
     */
    @ElementCollection
    @CollectionTable(name = "project_tags", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    /**
     * 프로젝트에서 사용된 기술 스택 목록
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectSkill> skills = new ArrayList<>();

    /**
     * 프로젝트 진행 기간
     */
    @Column
    private String duration;

    /**
     * 프로젝트 진행 회사/기관
     */
    @Column
    private String company;

    /**
     * 프로젝트 표시 순서
     */
    @Column(name = "display_order")
    private Integer order = 0;

    /**
     * 프로젝트 관련 링크 목록 (GitHub, 배포 URL 등)
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectLink> links = new ArrayList<>();

    /**
     * 프로젝트 관련 Q&A 목록
     */
    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectQnA> qnaList = new ArrayList<>();

    /**
     * 프로젝트 생성자
     * @param title 프로젝트 제목
     * @param category 프로젝트 카테고리
     * @param status 프로젝트 상태
     * @param description 프로젝트 설명
     * @param image 프로젝트 이미지 경로
     * @param tags 프로젝트 태그 목록
     * @param duration 프로젝트 진행 기간
     * @param company 프로젝트 진행 회사/기관
     * @param order 프로젝트 표시 순서
     */
    public Project(String title, String category, ProjectStatus status, String description,
                   String image, List<String> tags, String duration, String company, Integer order) {
        this.title = title;
        this.category = category;
        this.status = status != null ? status : ProjectStatus.DRAFT;
        this.description = description;
        this.image = image;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.duration = duration;
        this.company = company;
        this.order = order != null ? order : 0;
    }
}