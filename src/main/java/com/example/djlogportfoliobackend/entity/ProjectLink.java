package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * 프로젝트와 관련된 링크 정보를 관리하는 엔티티
 * GitHub, 데모 사이트, 문서 등의 링크를 저장
 */
@Entity
@Table(name = "project_link")
@Getter
@Setter
@NoArgsConstructor
public class ProjectLink {

    /** 링크의 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** 링크가 속한 프로젝트 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    private Project project;

    /** 링크의 표시 이름 (예: "GitHub", "Demo", "Documentation") */
    @Column(nullable = false)
    @NotBlank(message = "링크 이름은 필수입니다")
    private String label;

    /** 링크 URL 주소 */
    @Column(nullable = false)
    @NotBlank(message = "링크 주소는 필수입니다")
    private String url;

    /** 링크에 대한 추가 설명 (선택사항) */
    @Column
    private String description;

    /**
     * ProjectLink 생성자
     * @param project 링크가 속할 프로젝트
     * @param label 링크의 표시 이름
     * @param url 링크 URL 주소
     * @param description 링크 설명 (선택사항)
     */
    public ProjectLink(Project project, String label, String url, String description) {
        this.project = project;
        this.label = label;
        this.url = url;
        this.description = description;
    }
}