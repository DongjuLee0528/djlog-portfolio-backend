package com.example.djlogportfoliobackend.controller;

import com.example.djlogportfoliobackend.dto.LoginRequest;
import com.example.djlogportfoliobackend.dto.LoginResponse;
import com.example.djlogportfoliobackend.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authService.login(loginRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.status(401).build();
        }
    }

    /**
     * 관리자 로그아웃
     * JWT 토큰을 블랙리스트에 추가하여 무효화합니다.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
        Map<String, String> response = new HashMap<>();

        try {
            String authHeader = request.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("error", "Missing or invalid authorization header");
                return ResponseEntity.badRequest().body(response);
            }

            String token = authHeader.substring(7); // "Bearer " 제거
            authService.logout(token);

            response.put("message", "로그아웃이 성공적으로 처리되었습니다.");
            return ResponseEntity.ok(response);

        } catch (RuntimeException e) {
            log.error("Logout failed: {}", e.getMessage());
            response.put("error", "로그아웃 처리 중 오류가 발생했습니다.");
            return ResponseEntity.status(500).body(response);
        }
    }
}