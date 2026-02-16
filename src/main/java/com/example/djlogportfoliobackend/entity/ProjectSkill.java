package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * 프로젝트에서 사용된 기술 스택 정보를 관리하는 엔티티
 *
 * 프로젝트와 기술 스택 간의 관계를 나타내며,
 * 해당 프로젝트에서 사용된 구체적인 기술의 이름과 카테고리를 저장한다.
 *
 * 주요 속성:
 * - name: 기술명 (Java, Spring Boot, React 등)
 * - category: 기술 카테고리 (Frontend, Backend, Database, DevOps 등)
 * - project: 이 기술을 사용한 프로젝트
 */
@Entity
@Table(name = "project_skill")
@Getter
@Setter
@NoArgsConstructor
public class ProjectSkill {

    /** 프로젝트 기술의 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** 기술명 (예: Java, Spring Boot, React) */
    @Column(nullable = false)
    @NotBlank(message = "기술명은 필수입니다")
    private String name;

    /** 기술 카테고리 (Frontend, Backend, Database, DevOps, Tools 등) */
    @Column(nullable = false)
    @NotBlank(message = "카테고리는 필수입니다")
    private String category;

    /** 이 기술이 사용된 프로젝트 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    /**
     * ProjectSkill 생성자
     * @param name 기술명
     * @param category 기술 카테고리
     * @param project 기술이 사용된 프로젝트
     */
    public ProjectSkill(String name, String category, Project project) {
        this.name = name;
        this.category = category;
        this.project = project;
    }
}