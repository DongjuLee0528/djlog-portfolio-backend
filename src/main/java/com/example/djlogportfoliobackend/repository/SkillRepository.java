package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 기술 스택 정보 데이터 액세스를 위한 Repository 인터페이스
 *
 * <p>개인이 보유한 기술 스택 및 도구에 대한 정보를 관리합니다.
 * 포트폴리오에서 기술적 역량을 보여주는 핵심 데이터입니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>기술 스택 CRUD 작업</li>
 *   <li>카테고리별 기술 분류 및 조회</li>
 *   <li>숙련도 수준별 관리</li>
 *   <li>기술명 기준 정렬 조회</li>
 * </ul>
 *
 * <p>기술 카테고리 예시:</p>
 * <ul>
 *   <li>Frontend: React, Vue, JavaScript, HTML/CSS</li>
 *   <li>Backend: Java, Spring Boot, Node.js</li>
 *   <li>Database: MySQL, PostgreSQL, Redis</li>
 *   <li>DevOps: Docker, AWS, Jenkins</li>
 *   <li>Tools: Git, IntelliJ, VSCode</li>
 * </ul>
 *
 * <p>정렬 방식: 카테고리별 그룹화 → 기술명 알파벳 순</p>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    /**
     * 모든 기술 스택 정보를 카테고리와 이름 순으로 오름차순 정렬하여 조회
     * 카테고리별로 그룹화되어 표시하기에 적합한 순서로 반환
     * @return 카테고리와 이름 오름차순으로 정렬된 기술 스택 리스트
     */
    List<Skill> findAllByOrderByCategoryAscNameAsc();
}