package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 포트폴리오 개인 프로필 정보를 저장하는 엔티티 클래스
 * 개인의 기본 정보, 기술, 학력, 자격증 등을 관리
 */
@Entity
@Table(name = "profile")
@Getter
@Setter
@NoArgsConstructor
public class Profile {

    /**
     * 프로필 고유 식별자
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * 이름 (필수)
     */
    @Column(nullable = false)
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    /**
     * 직업/직책
     */
    @Column
    private String job;

    /**
     * 간단한 자기소개
     */
    @Column
    private String bio;

    /**
     * 상세한 자기소개
     */
    @Column(columnDefinition = "TEXT")
    private String about;

    /**
     * 프로필 이미지 경로
     */
    @Column
    private String image;

    /**
     * 이메일 주소
     */
    @Column
    @Email(message = "올바른 이메일 형식이어야 합니다")
    private String email;

    /**
     * GitHub 계정 URL
     */
    @Column
    private String github;

    /**
     * 이력서 파일 경로
     */
    @Column
    private String resume;

    /**
     * 보유 기술 목록
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Skill> skills = new ArrayList<>();

    /**
     * 학력 목록
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Education> educations = new ArrayList<>();

    /**
     * 자격증 목록
     */
    @OneToMany(mappedBy = "profile", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Certificate> certificates = new ArrayList<>();

    /**
     * 프로필 생성자
     * @param name 이름
     * @param bio 간단한 자기소개
     * @param about 상세한 자기소개
     * @param image 프로필 이미지 경로
     * @param email 이메일 주소
     * @param github GitHub 계정 URL
     */
    public Profile(String name, String bio, String about, String image, String email, String github) {
        this.name = name;
        this.bio = bio;
        this.about = about;
        this.image = image;
        this.email = email;
        this.github = github;
    }
}