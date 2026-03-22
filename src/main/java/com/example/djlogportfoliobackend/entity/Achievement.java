package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * 리더십, 멘토링, 수상 등의 성과/경험 정보를 저장하는 엔티티입니다.
 */
@Entity
@Table(name = "achievement")
@Getter
@Setter
@NoArgsConstructor
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "성과 제목은 필수입니다")
    private String title;

    @Column
    private String organization;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String period;

    @Column
    private String category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public Achievement(String title, String organization, String description, String period, String category, Profile profile) {
        this.title = title;
        this.organization = organization;
        this.description = description;
        this.period = period;
        this.category = category;
        this.profile = profile;
    }
}
