package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.ProjectLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 링크 데이터 접근을 위한 Repository 인터페이스
 * 프로젝트별 관련 링크 정보에 대한 CRUD 작업을 제공합니다.
 */
@Repository
public interface ProjectLinkRepository extends JpaRepository<ProjectLink, UUID> {

    /**
     * 특정 프로젝트의 모든 링크 조회
     * @param projectId 조회할 프로젝트 ID
     * @return 해당 프로젝트의 링크 목록
     */
    List<ProjectLink> findByProjectId(UUID projectId);

    /**
     * 특정 프로젝트의 모든 링크 삭제
     * @param projectId 삭제할 프로젝트 ID
     */
    void deleteByProjectId(UUID projectId);
}