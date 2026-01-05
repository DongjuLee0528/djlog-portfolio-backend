package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 관리자 계정 데이터 접근을 위한 Repository 인터페이스
 * 관리자 인증 및 계정 관리 기능을 제공합니다.
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * 이메일로 관리자 계정 조회
     * @param email 조회할 관리자 이메일
     * @return 해당 이메일의 관리자 계정 (Optional)
     */
    Optional<Admin> findByEmail(String email);
}