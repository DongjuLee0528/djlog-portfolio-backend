package com.example.djlogportfoliobackend.service;

import com.example.djlogportfoliobackend.dto.LoginRequest;
import com.example.djlogportfoliobackend.dto.LoginResponse;
import com.example.djlogportfoliobackend.entity.Admin;
import com.example.djlogportfoliobackend.exception.AuthenticationException;
import com.example.djlogportfoliobackend.repository.AdminRepository;
import com.example.djlogportfoliobackend.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService 단위 테스트")
class AuthServiceTest {

    @Mock
    private AdminRepository adminRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private SessionManagementService sessionManagementService;

    @InjectMocks
    private AuthService authService;

    private Admin testAdmin;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        testAdmin = new Admin("test@example.com", "encodedPassword");
        loginRequest = new LoginRequest("test@example.com", "plainPassword");
    }

    @Test
    @DisplayName("로그인 성공 - 올바른 이메일과 비밀번호")
    void login_Success_ValidCredentials() {
        // Given
        String expectedToken = "generated-jwt-token";
        String expectedTokenId = "token123";

        when(adminRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches(loginRequest.getPassword(), testAdmin.getPassword()))
                .thenReturn(true);
        when(jwtUtil.generateTokenWithId(testAdmin.getEmail()))
                .thenReturn(expectedToken);
        when(jwtUtil.extractTokenId(expectedToken))
                .thenReturn(expectedTokenId);

        // When
        LoginResponse response = authService.login(loginRequest);

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo(expectedToken);

        verify(adminRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testAdmin.getPassword());
        verify(jwtUtil).generateTokenWithId(testAdmin.getEmail());
        verify(sessionManagementService).createSession(eq(testAdmin.getEmail()), eq(expectedTokenId), any());
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_Failure_UserNotFound() {
        // Given
        when(adminRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.empty());
        when(passwordEncoder.matches(anyString(), anyString()))
                .thenReturn(false); // 더미 해시 검증

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("잘못된 이메일 또는 비밀번호입니다.");

        verify(adminRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(anyString(), anyString()); // 타이밍 공격 방지를 위한 더미 검증
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_Failure_InvalidPassword() {
        // Given
        when(adminRepository.findByEmail(loginRequest.getEmail()))
                .thenReturn(Optional.of(testAdmin));
        when(passwordEncoder.matches(loginRequest.getPassword(), testAdmin.getPassword()))
                .thenReturn(false);

        // When & Then
        assertThatThrownBy(() -> authService.login(loginRequest))
                .isInstanceOf(AuthenticationException.class)
                .hasMessageContaining("잘못된 이메일 또는 비밀번호입니다.");

        verify(adminRepository).findByEmail(loginRequest.getEmail());
        verify(passwordEncoder).matches(loginRequest.getPassword(), testAdmin.getPassword());
        verifyNoInteractions(jwtUtil);
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_Success() {
        // Given
        String token = "valid-jwt-token";
        String tokenId = "token123";

        when(jwtUtil.extractTokenId(token)).thenReturn(tokenId);

        // When
        authService.logout(token);

        // Then
        verify(jwtUtil).invalidateToken(token);
        verify(jwtUtil).extractTokenId(token);
        verify(sessionManagementService).removeSession(tokenId);
    }

    @Test
    @DisplayName("관리자 계정 생성 성공")
    void createAdmin_Success() {
        // Given
        String email = "admin@example.com";
        String rawPassword = "plainPassword";
        String encodedPassword = "encodedPassword";
        Admin savedAdmin = new Admin(email, encodedPassword);

        when(passwordEncoder.encode(rawPassword)).thenReturn(encodedPassword);
        when(adminRepository.save(any(Admin.class))).thenReturn(savedAdmin);

        // When
        Admin result = authService.createAdmin(email, rawPassword);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(email);
        assertThat(result.getPassword()).isEqualTo(encodedPassword);

        verify(passwordEncoder).encode(rawPassword);
        verify(adminRepository).save(any(Admin.class));
    }
}