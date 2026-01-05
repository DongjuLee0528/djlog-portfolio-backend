package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "project_qna")
@Getter
@Setter
@NoArgsConstructor
public class ProjectQnA {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    private Project project;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "질문은 필수입니다")
    private String question;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "답변은 필수입니다")
    private String answer;

    public ProjectQnA(Project project, String question, String answer) {
        this.project = project;
        this.question = question;
        this.answer = answer;
    }
}