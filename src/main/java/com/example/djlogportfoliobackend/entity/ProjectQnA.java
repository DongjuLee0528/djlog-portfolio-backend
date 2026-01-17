package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * 프로젝트 Q&A 정보를 관리하는 엔티티
 * 프로젝트에 대한 자주 묻는 질문과 답변을 저장
 */
@Entity
@Table(name = "project_qna")
@Getter
@Setter
@NoArgsConstructor
public class ProjectQnA {

    /** Q&A의 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    /** Q&A가 속한 프로젝트 */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", nullable = false)
    @NotNull
    private Project project;

    /** 질문 내용 */
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "질문은 필수입니다")
    private String question;

    /** 질문에 대한 답변 내용 */
    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "답변은 필수입니다")
    private String answer;

    /**
     * ProjectQnA 생성자
     * @param project Q&A가 속할 프로젝트
     * @param question 질문 내용
     * @param answer 답변 내용
     */
    public ProjectQnA(Project project, String question, String answer) {
        this.project = project;
        this.question = question;
        this.answer = answer;
    }
}