package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "education")
@Getter
@Setter
@NoArgsConstructor
public class Education {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "학교명은 필수입니다")
    private String school;

    @Column
    private String major;

    @Column
    private String period;

    @Column
    private String degree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profile_id")
    private Profile profile;

    public Education(String school, String major, String period, String degree, Profile profile) {
        this.school = school;
        this.major = major;
        this.period = period;
        this.degree = degree;
        this.profile = profile;
    }
}