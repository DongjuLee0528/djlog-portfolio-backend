package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.LoginRequest;
import com.example.djlogportfoliobackend.dto.LoginResponse;
import com.example.djlogportfoliobackend.entity.Admin;
import com.example.djlogportfoliobackend.exception.AuthenticationException;
import com.example.djlogportfoliobackend.repository.AdminRepository;
import com.example.djlogportfoliobackend.service.SessionManagementService.SessionInfo;
import com.example.djlogportfoliobackend.util.JwtUtil;
import com.example.djlogportfoliobackend.util.NetworkUtil;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    private final SessionManagementService sessionManagementService;

    // 타이밍 공격 방지를 위한 유효한 BCrypt 해시 (더미 비밀번호: "dummyPasswordForTimingAttackPrevention")
    private static final String DUMMY_PASSWORD_HASH = "$2a$10$N9qo8uLOickgx2ZMRZoMye1mIWdO6aDIg8XrqkZWJbv6WQ8kJjX0G";

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

        Admin admin = findAdminByEmail(loginRequest.getEmail());
        boolean isValidCredentials = authenticateUser(admin, loginRequest.getPassword());
        String token = null;

        if (isValidCredentials) {
            token = generateTokenAndCreateSession(admin, traceId);
        }

        validateCredentialsOrThrow(isValidCredentials, traceId, loginRequest.getEmail());
        return new LoginResponse(token);
    }

    /**
     * 이메일로 관리자 조회
     */
    private Admin findAdminByEmail(String email) {
        return adminRepository.findByEmail(email).orElse(null);
    }

    /**
     * 사용자 인증 처리 (타이밍 공격 방지 포함)
     */
    private boolean authenticateUser(Admin admin, String password) {
        if (admin != null) {
            return passwordEncoder.matches(password, admin.getPassword());
        } else {
            // 사용자가 존재하지 않는 경우: 동일한 시간 소모를 위한 더미 검증
            // 타이밍 공격 방지 목적 (유효한 BCrypt 해시 사용)
            passwordEncoder.matches(password, DUMMY_PASSWORD_HASH);
            return false;
        }
    }

    /**
     * 토큰 생성 및 세션 생성
     */
    private String generateTokenAndCreateSession(Admin admin, String traceId) {
        String token = jwtUtil.generateTokenWithId(admin.getEmail());

        // 세션 관리 - 클라이언트 정보 수집 및 세션 생성
        try {
            String tokenId = jwtUtil.extractTokenId(token);
            SessionInfo clientInfo = getClientInfo();
            sessionManagementService.createSession(admin.getEmail(), tokenId, clientInfo);
        } catch (Exception e) {
            log.warn("[AUTH] Failed to create session for user {}: {}", admin.getEmail(), e.getMessage());
        }

        log.info("[AUTH] Login successful - TraceId: {} - Email: {}", traceId, admin.getEmail());
        return token;
    }

    /**
     * 인증 결과 검증 및 예외 처리
     */
    private void validateCredentialsOrThrow(boolean isValidCredentials, String traceId, String email) {
        if (!isValidCredentials) {
            log.warn("[AUTH] Login failed - TraceId: {} - Invalid credentials for: {}", traceId, email);
            throw new AuthenticationException("잘못된 이메일 또는 비밀번호입니다.");
        }
    }

    /**
     * 관리자 로그아웃 처리
     * JWT 토큰을 블랙리스트에 추가하여 무효화합니다.
     *
     * @param token 무효화할 JWT 토큰
     */
    public void logout(String token) {
        String traceId = MDC.get("traceId");
        log.info("[AUTH] Logout request - TraceId: {}", traceId);

        try {
            // 1. JWT 토큰을 블랙리스트에 추가
            jwtUtil.invalidateToken(token);

            // 2. 세션 제거
            try {
                String tokenId = jwtUtil.extractTokenId(token);
                sessionManagementService.removeSession(tokenId);
            } catch (Exception e) {
                log.warn("[AUTH] Failed to remove session during logout: {}", e.getMessage());
            }

            log.info("[AUTH] Logout successful - TraceId: {}", traceId);
        } catch (Exception e) {
            log.error("[AUTH] Logout failed - TraceId: {} - Error: {}", traceId, e.getMessage(), e);
            throw new RuntimeException("로그아웃 처리 중 오류가 발생했습니다.");
        }
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

    /**
     * 클라이언트 정보 수집
     *
     * @return 클라이언트 정보
     */
    private SessionInfo getClientInfo() {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs != null) {
                HttpServletRequest request = attrs.getRequest();
                String clientIp = NetworkUtil.getClientIpAddress(request);
                String userAgent = request.getHeader("User-Agent");

                return SessionInfo.builder()
                        .clientIp(clientIp != null ? clientIp : "unknown")
                        .userAgent(userAgent != null ? userAgent : "unknown")
                        .build();
            }
        } catch (Exception e) {
            log.debug("Failed to get client info: {}", e.getMessage());
        }

        return SessionInfo.builder()
                .clientIp("unknown")
                .userAgent("unknown")
                .build();
    }
}