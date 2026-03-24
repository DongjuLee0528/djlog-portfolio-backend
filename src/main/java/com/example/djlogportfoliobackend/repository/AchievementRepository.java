package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

/**
 * 성과/경험 정보 데이터 액세스를 위한 Repository 인터페이스
 *
 * <p>사용자의 성과, 경험, 수상 내역, 리더십 활동 등의 정보에 대한
 * 데이터베이스 접근을 담당합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>성과/경험 정보 CRUD 작업</li>
 *   <li>프로필별 성과 정보 조회</li>
 *   <li>카테고리별 성과 분류</li>
 * </ul>
 *
 * <p>저장되는 성과 유형:</p>
 * <ul>
 *   <li>수상 내역 (대회, 공모전 등)</li>
 *   <li>리더십 활동 (프로젝트 리더, 팀장 등)</li>
 *   <li>멘토링 활동</li>
 *   <li>기타 특별한 경험</li>
 * </ul>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
public interface AchievementRepository extends JpaRepository<Achievement, UUID> {
}
