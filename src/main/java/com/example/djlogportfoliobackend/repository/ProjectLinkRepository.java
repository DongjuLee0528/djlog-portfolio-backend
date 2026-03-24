package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.ProjectLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 링크 데이터 접근을 위한 Repository 인터페이스
 *
 * <p>각 프로젝트와 관련된 외부 링크들을 관리합니다.
 * GitHub 저장소, 데모 사이트, 문서, 발표 자료 등의 링크 정보를 저장하고 조회합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>프로젝트별 링크 정보 CRUD 작업</li>
 *   <li>프로젝트 ID 기준 링크 목록 조회</li>
 *   <li>프로젝트 삭제 시 연관 링크 일괄 삭제</li>
 * </ul>
 *
 * <p>관리되는 링크 유형:</p>
 * <ul>
 *   <li>GitHub 저장소 링크</li>
 *   <li>라이브 데모/배포 사이트</li>
 *   <li>API 문서 (Swagger, Postman 등)</li>
 *   <li>발표 자료 (PPT, PDF 등)</li>
 *   <li>관련 블로그 포스트</li>
 *   <li>기타 참고 자료</li>
 * </ul>
 *
 * <p>데이터 무결성: 프로젝트 삭제 시 관련 링크 자동 삭제 (Cascade)</p>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
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