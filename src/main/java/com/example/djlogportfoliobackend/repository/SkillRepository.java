package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 기술 스택 정보 데이터 액세스 인터페이스
 * Skill 엔티티에 대한 CRUD 작업 및 커스텀 쿼리를 제공합니다.
 */
public interface SkillRepository extends JpaRepository<Skill, UUID> {

    /**
     * 모든 기술 스택 정보를 카테고리와 이름 순으로 오름차순 정렬하여 조회
     * 카테고리별로 그룹화되어 표시하기에 적합한 순서로 반환
     * @return 카테고리와 이름 오름차순으로 정렬된 기술 스택 리스트
     */
    List<Skill> findAllByOrderByCategoryAscNameAsc();
}