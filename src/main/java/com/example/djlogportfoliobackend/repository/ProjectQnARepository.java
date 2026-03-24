package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.ProjectQnA;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 Q&A 데이터 접근을 위한 Repository 인터페이스
 *
 * <p>각 프로젝트의 주요 질문과 답변을 관리합니다.
 * 프로젝트 상세 페이지에서 "자주 묻는 질문", "기술적 고민", "프로젝트 회고" 등의
 * 구조화된 질의응답 콘텐츠를 제공합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>프로젝트별 Q&A CRUD 작업</li>
 *   <li>표시 순서(displayOrder) 기반 정렬 조회</li>
 *   <li>Q&A 순서 일괄 변경 지원</li>
 *   <li>프로젝트 삭제 시 연관 Q&A 일괄 삭제</li>
 * </ul>
 *
 * <p>Q&A 유형 예시:</p>
 * <ul>
 *   <li>프로젝트 목적과 배경</li>
 *   <li>사용한 기술 선택 이유</li>
 *   <li>구현 과정에서의 어려움과 해결책</li>
 *   <li>성능 최적화 방법</li>
 *   <li>아쉬운 점과 개선 방향</li>
 * </ul>
 *
 * <p>정렬 방식: displayOrder → id (ASC)</p>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
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
     * 특정 프로젝트의 모든 Q&A를 표시 순서 기준으로 조회
     * @param projectId 조회할 프로젝트 ID
     * @return 정렬된 Q&A 목록
     */
    List<ProjectQnA> findByProjectIdOrderByDisplayOrderAscIdAsc(UUID projectId);

    /**
     * 특정 프로젝트에 속한 일부 Q&A를 조회
     * @param projectId 프로젝트 ID
     * @param ids 조회할 Q&A ID 목록
     * @return 조회된 Q&A 목록
     */
    List<ProjectQnA> findByProjectIdAndIdIn(UUID projectId, Collection<UUID> ids);

    /**
     * 특정 프로젝트의 모든 Q&A 삭제
     * @param projectId 삭제할 프로젝트 ID
     */
    void deleteByProjectId(UUID projectId);
}
