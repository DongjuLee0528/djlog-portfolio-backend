package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Education;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 학력 정보 데이터 액세스 인터페이스
 * Education 엔티티에 대한 CRUD 작업 및 커스텀 쿼리를 제공합니다.
 */
public interface EducationRepository extends JpaRepository<Education, UUID> {

    /**
     * 모든 학력 정보를 기간순으로 내림차순 정렬하여 조회
     * 가장 최근 학력부터 오래된 학력 순서로 반환
     * @return 기간 내림차순으로 정렬된 학력 리스트
     */
    List<Education> findAllByOrderByPeriodDesc();
}