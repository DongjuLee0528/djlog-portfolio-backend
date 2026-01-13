package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "project_skill")
@Getter
@Setter
@NoArgsConstructor
public class ProjectSkill {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "기술명은 필수입니다")
    private String name;

    @Column(nullable = false)
    @NotBlank(message = "카테고리는 필수입니다")
    private String category; // Frontend, Backend, Database, DevOps, Tools, etc.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id")
    private Project project;

    public ProjectSkill(String name, String category, Project project) {
        this.name = name;
        this.category = category;
        this.project = project;
    }
}