package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "project_link")
@Getter
@Setter
@NoArgsConstructor
public class ProjectLink {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    private Project project;

    @Column(nullable = false)
    @NotBlank(message = "링크 이름은 필수입니다")
    private String label;

    @Column(nullable = false)
    @NotBlank(message = "링크 주소는 필수입니다")
    private String url;

    @Column
    private String description;

    public ProjectLink(Project project, String label, String url, String description) {
        this.project = project;
        this.label = label;
        this.url = url;
        this.description = description;
    }
}