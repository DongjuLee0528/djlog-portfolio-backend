package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.ProjectQnA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 Q&A 데이터 접근을 위한 Repository 인터페이스
 * 프로젝트별 질문과 답변 정보에 대한 CRUD 작업을 제공합니다.
 */
@Repository
public interface ProjectQnARepository extends JpaRepository<ProjectQnA, UUID> {

    /**
     * 특정 프로젝트의 모든 Q&A 조회
     * @param projectId 조회할 프로젝트 ID
     * @return 해당 프로젝트의 Q&A 목록
     */
    List<ProjectQnA> findByProjectId(UUID projectId);

    /**
     * 특정 프로젝트의 모든 Q&A 삭제
     * @param projectId 삭제할 프로젝트 ID
     */
    void deleteByProjectId(UUID projectId);
}