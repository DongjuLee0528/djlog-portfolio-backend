package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

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