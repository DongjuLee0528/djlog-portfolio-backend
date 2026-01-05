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
     *
     * @param loginRequest 로그인 요청 정보 (이메일, 비밀번호)
     * @return 로그인 응답 (JWT 토큰)
     * @throws RuntimeException 인증 실패 시
     */
    public LoginResponse login(LoginRequest loginRequest) {
        String traceId = MDC.get("traceId");
        log.info("[AUTH] Login attempt - TraceId: {} - Email: {}", traceId, loginRequest.getEmail());

        Admin admin = adminRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> {
                    log.warn("[AUTH] Login failed - TraceId: {} - User not found: {}", traceId, loginRequest.getEmail());
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            log.warn("[AUTH] Login failed - TraceId: {} - Invalid password for user: {}", traceId, loginRequest.getEmail());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(admin.getEmail());
        log.info("[AUTH] Login successful - TraceId: {} - Email: {}", traceId, admin.getEmail());
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