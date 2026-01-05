package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "project")
@Getter
@Setter
@NoArgsConstructor
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    @NotBlank(message = "프로젝트 제목은 필수입니다")
    private String title;

    @Column
    private String category;

    @Enumerated(EnumType.STRING)
    @Column
    private ProjectStatus status = ProjectStatus.DRAFT;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column
    private String image;

    @ElementCollection
    @CollectionTable(name = "project_tags", joinColumns = @JoinColumn(name = "project_id"))
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

    @Column(name = "display_order")
    private Integer order = 0;

    @OneToMany(mappedBy = "project", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProjectLink> links = new ArrayList<>();

    public Project(String title, String category, ProjectStatus status, String description,
                   String image, List<String> tags, Integer order) {
        this.title = title;
        this.category = category;
        this.status = status != null ? status : ProjectStatus.DRAFT;
        this.description = description;
        this.image = image;
        this.tags = tags != null ? tags : new ArrayList<>();
        this.order = order != null ? order : 0;
    }
}