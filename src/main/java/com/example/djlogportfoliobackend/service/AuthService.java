package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.LoginRequest;
import com.example.djlogportfoliobackend.dto.LoginResponse;
import com.example.djlogportfoliobackend.entity.Admin;
import com.example.djlogportfoliobackend.repository.AdminRepository;
import com.example.djlogportfoliobackend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.info("[AUTH] Login attempt for username: {}", loginRequest.getUsername());

        Admin admin = adminRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> {
                    log.warn("[AUTH] Login failed - User not found: {}", loginRequest.getUsername());
                    return new RuntimeException("Invalid credentials");
                });

        if (!passwordEncoder.matches(loginRequest.getPassword(), admin.getPassword())) {
            log.warn("[AUTH] Login failed - Invalid password for user: {}", loginRequest.getUsername());
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(admin.getUsername());
        log.info("[AUTH] Login successful for user: {}", admin.getUsername());
        return new LoginResponse(token, admin.getUsername());
    }

    public Admin createAdmin(String username, String rawPassword) {
        log.info("[AUTH] Creating new admin user: {}", username);
        String encodedPassword = passwordEncoder.encode(rawPassword);
        Admin admin = new Admin(username, encodedPassword);
        Admin savedAdmin = adminRepository.save(admin);
        log.info("[AUTH] Admin user created successfully: {}", username);
        return savedAdmin;
    }
}