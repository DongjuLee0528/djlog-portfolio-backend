package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * 기술 스택 정보를 관리하는 엔티티
 *
 * 사용자가 보유한 기술 스택의 이름, 카테고리, 숙련도 정보를 저장한다.
 * 프로필과 N:1 관계를 갖는다.
 *
 * 주요 속성:
 * - name: 기술명 (Java, Spring Boot, React 등)
 * - category: 기술 카테고리 (Frontend, Backend, Database, DevOps 등)
 * - proficiency: 숙련도 수준 (Beginner, Intermediate, Advanced, Expert)
 * - profile: 소유자 프로필
 */
@Entity
@Table(name = "skill")
@Getter
@Setter
@NoArgsConstructor
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "기술명은 필수입니다")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "카테고리는 필수입니다")
    private String category; // Frontend, Backend, Database, DevOps, Tools, etc.

    @Column
    private String proficiency; // Beginner, Intermediate, Advanced, Expert

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public Skill(String name, String category, String proficiency, Profile profile) {
        this.name = name;
        this.category = category;
        this.proficiency = proficiency;
        this.profile = profile;
    }
}