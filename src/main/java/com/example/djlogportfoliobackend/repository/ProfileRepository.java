package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * 프로필 데이터 접근을 위한 Repository 인터페이스
 *
 * <p>포트폴리오 개인 프로필 정보에 대한 데이터베이스 접근을 담당합니다.
 * 프로필은 포트폴리오 웹사이트의 핵심 데이터로, 일반적으로 단일 인스턴스만 존재합니다.</p>
 *
 * <p>주요 특징:</p>
 * <ul>
 *   <li>개인 포트폴리오 특성상 단일 프로필만 관리</li>
 *   <li>프로필 정보는 메인 페이지, About 페이지에서 활용</li>
 *   <li>관리자만 프로필 정보 수정 가능</li>
 * </ul>
 *
 * <p>포함되는 관련 엔티티:</p>
 * <ul>
 *   <li>Skill - 보유 기술 스택 (@OneToMany)</li>
 *   <li>Education - 학력 정보 (@OneToMany)</li>
 *   <li>Certificate - 자격증 정보 (@OneToMany)</li>
 * </ul>
 *
 * <p>현재는 기본 JPA 메서드만 사용하며, 필요시 커스텀 쿼리 추가 가능합니다.</p>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
@Repository
public interface ProfileRepository extends JpaRepository<Profile, UUID> {
}