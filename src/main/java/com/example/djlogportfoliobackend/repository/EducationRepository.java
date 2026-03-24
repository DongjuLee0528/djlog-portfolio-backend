package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 학력 정보 데이터 액세스를 위한 Repository 인터페이스
 *
 * <p>사용자의 교육 경력 및 학력 정보에 대한 데이터베이스 접근을 담당합니다.
 * 대학교, 대학원, 기타 교육기관에서의 학습 경험을 관리합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>학력 정보 CRUD 작업</li>
 *   <li>기간별 학력 정보 정렬 조회</li>
 *   <li>프로필별 학력 연관 관리</li>
 * </ul>
 *
 * <p>관리되는 교육 정보:</p>
 * <ul>
 *   <li>대학교/대학원 정규 과정</li>
 *   <li>부트캠프, 온라인 과정</li>
 *   <li>전문 교육기관 수료</li>
 *   <li>기타 학습 경험</li>
 * </ul>
 *
 * <p>정렬 기본값: 최신 교육 경험부터 과거 순서로 표시</p>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
public interface EducationRepository extends JpaRepository<Education, UUID> {

    /**
     * 모든 학력 정보를 기간순으로 내림차순 정렬하여 조회
     * 가장 최근 학력부터 오래된 학력 순서로 반환
     * @return 기간 내림차순으로 정렬된 학력 리스트
     */
    List<Education> findAllByOrderByPeriodDesc();
}