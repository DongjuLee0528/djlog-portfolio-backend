package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.LoginRequest;
import com.example.djlogportfoliobackend.dto.LoginResponse;
import com.example.djlogportfoliobackend.entity.Admin;
import com.example.djlogportfoliobackend.repository.AdminRepository;
import com.example.djlogportfoliobackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * 관리자 인증 서비스
 * 관리자 로그인, 계정 생성 등 인증 관련 비즈니스 로직을 처리합니다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 관리자 로그인 처리
     * 이메일과 비밀번호를 검증하고 JWT 토큰을 생성합니다.
     * 타이밍 공격 방지를 위해 사용자 존재 여부와 관계없이 동일한 처리 시간을 보장합니다.
     *
     * @param loginRequest 로그인 요청 정보 (이메일, 비밀번호)
     * @return 로그인 응답 (JWT 토큰)
     * @throws RuntimeException 인증 실패 시
     */
    public LoginResponse login(LoginRequest loginRequest) {
        String traceId = MDC.get("traceId");
        log.info("[AUTH] Login attempt - TraceId: {} - Email: {}", traceId, loginRequest.getEmail());

        boolean isValidCredentials = false;
        String token = null;

        // 사용자 조회
        Admin admin = adminRepository.findByEmail(loginRequest.getEmail()).orElse(null);

        if (admin != null) {
            // 사용자가 존재하는 경우: 실제 비밀번호 검증
            isValidCredentials = passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword());
            if (isValidCredentials) {
                token = jwtUtil.generateToken(admin.getEmail());
                log.info("[AUTH] Login successful - TraceId: {} - Email: {}", traceId, admin.getEmail());
            }
        } else {
            // 사용자가 존재하지 않는 경우: 동일한 시간 소모를 위한 더미 검증
            // 타이밍 공격 방지 목적
            passwordEncoder.matches(loginRequest.getPassword(), "$2a$10$dummyHashToPreventTimingAttacks");
        }

        if (!isValidCredentials) {
            log.warn("[AUTH] Login failed - TraceId: {} - Invalid credentials for: {}", traceId, loginRequest.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        return new LoginResponse(token);
    }

    /**
     * 새로운 관리자 계정 생성
     * 이메일과 암호화된 비밀번호로 관리자 계정을 생성합니다.
     *
     * @param email 관리자 이메일
     * @param rawPassword 평문 비밀번호 (암호화 처리됨)
     * @return 생성된 관리자 엔티티
     * @throws Exception 계정 생성 실패 시
     */
    public Admin createAdmin(String email, String rawPassword) {
        String traceId = MDC.get("traceId");
        log.info("[AUTH] Creating new admin user - TraceId: {} - Email: {}", traceId, email);

        try {
            String encodedPassword = passwordEncoder.encode(rawPassword);
            Admin admin = new Admin(email, encodedPassword);
            Admin savedAdmin = adminRepository.save(admin);
            log.info("[AUTH] Admin user created successfully - TraceId: {} - Email: {}", traceId, email);
            return savedAdmin;
        } catch (Exception e) {
            log.error("[AUTH] Failed to create admin user - TraceId: {} - Email: {} - Error: {}",
                    traceId, email, e.getMessage(), e);
            throw e;
        }
    }
}