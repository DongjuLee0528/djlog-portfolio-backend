package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

/**
 * 자격증 엔티티
 * 사용자의 자격증 정보를 저장하는 데이터베이스 엔티티입니다.
 */
@Entity
@Table(name = "certificate")
@Getter
@Setter
@NoArgsConstructor
public class Certificate {

    /**
     * 자격증 고유 ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /**
     * 자격증명
     * 자격증의 정식 명칭입니다.
     */
    @Column(nullable = false)
    @NotBlank(message = "자격증명은 필수입니다")
    private String name;

    /**
     * 발급기관
     * 자격증을 발급한 기관명입니다.
     */
    @Column
    private String issuer;

    /**
     * 발급일자
     * 자격증이 발급된 날짜입니다.
     */
    @Column
    private LocalDate issueDate;

    /**
     * 자격증 ID
     * 발급기관에서 부여한 자격증의 고유 식별번호입니다.
     */
    @Column
    private String credentialId;

    /**
     * 프로필 연관관계
     * 이 자격증이 속한 사용자 프로필입니다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    /**
     * 자격증 생성자
     * 모든 필드를 받아 자격증 객체를 생성합니다.
     *
     * @param name 자격증명
     * @param issuer 발급기관
     * @param issueDate 발급일자
     * @param credentialId 자격증 ID
     * @param profile 연관된 프로필
     */
    public Certificate(String name, String issuer, LocalDate issueDate, String credentialId, Profile profile) {
        this.name = name;
        this.issuer = issuer;
        this.issueDate = issueDate;
        this.credentialId = credentialId;
        this.profile = profile;
    }
}