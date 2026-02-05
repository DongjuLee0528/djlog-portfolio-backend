package com.example.djlogportfoliobackend.repository;

import com.example.djlogportfoliobackend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 관리자 계정 데이터 접근을 위한 Repository 인터페이스
 *
 * <p>포트폴리오 관리자 계정에 대한 데이터베이스 접근을 담당합니다.
 * 관리자 인증, 계정 생성, 조회 등의 기능을 제공합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>이메일 기반 관리자 계정 조회 (로그인 시 사용)</li>
 *   <li>관리자 계정 생성 및 관리</li>
 *   <li>계정 존재 여부 확인</li>
 * </ul>
 *
 * <p>보안 고려사항:</p>
 * <ul>
 *   <li>이메일은 unique 제약조건으로 중복 방지</li>
 *   <li>비밀번호는 BCrypt로 암호화되어 저장</li>
 *   <li>관리자 계정은 최소한으로 유지</li>
 * </ul>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    /**
     * 이메일로 관리자 계정 조회
     *
     * <p>로그인 인증 시 사용되는 핵심 메서드입니다.
     * 입력받은 이메일과 일치하는 관리자 계정을 데이터베이스에서 조회합니다.</p>
     *
     * @param email 조회할 관리자 이메일 (대소문자 구분)
     * @return 해당 이메일의 관리자 계정 (Optional로 안전한 null 처리)
     */
    Optional<Admin> findByEmail(String email);
}