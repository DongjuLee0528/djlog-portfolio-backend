package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * 자격증 정보 데이터 액세스 인터페이스
 * Certificate 엔티티에 대한 CRUD 작업 및 커스텀 쿼리를 제공합니다.
 */
public interface CertificateRepository extends JpaRepository<Certificate, UUID> {

    /**
     * 모든 자격증 정보를 발급일 기준 내림차순 정렬하여 조회
     * 가장 최근 발급된 자격증부터 오래된 자격증 순서로 반환
     * @return 발급일 내림차순으로 정렬된 자격증 리스트
     */
    List<Certificate> findAllByOrderByIssueDateDesc();
}