package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Project;
import com.example.djlogportfoliobackend.entity.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 데이터 접근을 위한 Repository 인터페이스
 * 프로젝트 엔티티에 대한 CRUD 작업과 커스텀 쿼리 메서드를 제공합니다.
 */
@Repository
public interface ProjectRepository extends JpaRepository<Project, UUID> {

    /**
     * 특정 상태의 프로젝트를 정렬 순서와 제목 순으로 조회
     * @param status 조회할 프로젝트 상태
     * @return 정렬된 프로젝트 목록
     */
    List<Project> findByStatusOrderByOrderAscTitleAsc(ProjectStatus status);

    /**
     * 모든 프로젝트를 정렬 순서와 제목 순으로 조회
     * @return 정렬된 전체 프로젝트 목록
     */
    List<Project> findAllByOrderByOrderAscTitleAsc();

    /**
     * 카테고리별 프로젝트를 정렬 순서와 제목 순으로 조회 (대소문자 무시)
     * @param category 조회할 카테고리명
     * @return 해당 카테고리의 정렬된 프로젝트 목록
     */
    List<Project> findByCategoryIgnoreCaseOrderByOrderAscTitleAsc(String category);

    /**
     * 특정 태그를 포함한 프로젝트를 정렬 순서와 제목 순으로 조회 (대소문자 무시)
     * @param tag 검색할 태그
     * @return 해당 태그를 포함한 정렬된 프로젝트 목록
     */
    List<Project> findByTagsContainingIgnoreCaseOrderByOrderAscTitleAsc(String tag);
}