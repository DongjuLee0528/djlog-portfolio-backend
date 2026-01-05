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

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

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