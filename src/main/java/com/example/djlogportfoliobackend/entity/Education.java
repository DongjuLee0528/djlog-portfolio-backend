package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * 학력 엔티티
 * 사용자의 학력 정보를 저장하는 데이터베이스 엔티티입니다.
 */
@Entity
@Table(name = "education")
@Getter
@Setter
@NoArgsConstructor
public class Education {

    /**
     * 학력 고유 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * 학교명
     * 교육기관의 정식 명칭입니다.
     */
    @Column(nullable = false)
    @NotBlank(message = "학교명은 필수입니다")
    private String school;

    /**
     * 전공/학과
     * 학습한 전공 분야 또는 학과명입니다.
     */
    @Column
    private String major;

    /**
     * 재학기간
     * 교육기관에서의 재학 기간입니다. (예: 2020.03 - 2024.02)
     */
    @Column
    private String period;

    /**
     * 학위
     * 취득한 학위 또는 졸업 구분입니다. (예: 학사, 석사, 박사, 졸업예정)
     */
    @Column
    private String degree;

    /**
     * 프로필 연관관계
     * 이 학력이 속한 사용자 프로필입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    /**
     * 학력 생성자
     * 모든 필드를 받아 학력 객체를 생성합니다.
     *
     * @param school 학교명
     * @param major 전공/학과
     * @param period 재학기간
     * @param degree 학위
     * @param profile 연관된 프로필
     */
    public Education(String school, String major, String period, String degree, Profile profile) {
        this.school = school;
        this.major = major;
        this.period = period;
        this.degree = degree;
        this.profile = profile;
    }
}