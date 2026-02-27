package com.example.djlogportfoliobackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@DisplayName("SecurityService 단위 테스트")
class SecurityServiceTest {

    private SecurityService securityService;
    private SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService();
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("인증된 사용자 확인 - 정상 인증")
    void isAuthenticated_Success_AuthenticatedUser() {
        // Given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin@example.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        boolean result = securityService.isAuthenticated();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("인증된 사용자 확인 - 익명 사용자")
    void isAuthenticated_Failure_AnonymousUser() {
        // Given
        AnonymousAuthenticationToken anonymousToken = new AnonymousAuthenticationToken(
                "key",
                "anonymousUser",
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))
        );
        when(securityContext.getAuthentication()).thenReturn(anonymousToken);

        // When
        boolean result = securityService.isAuthenticated();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("인증된 사용자 확인 - 인증 정보 없음")
    void isAuthenticated_Failure_NoAuthentication() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        boolean result = securityService.isAuthenticated();

        // Then
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("현재 사용자명 조회 - 인증된 사용자")
    void getCurrentUsername_Success_AuthenticatedUser() {
        // Given
        String expectedUsername = "admin@example.com";
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                expectedUsername,
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        String result = securityService.getCurrentUsername();

        // Then
        assertThat(result).isEqualTo(expectedUsername);
    }

    @Test
    @DisplayName("현재 사용자명 조회 - 비인증 사용자")
    void getCurrentUsername_Failure_UnauthenticatedUser() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        String result = securityService.getCurrentUsername();

        // Then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("관리자 권한 확인 - 인증된 사용자")
    void isAdmin_Success_AuthenticatedUser() {
        // Given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin@example.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        boolean result = securityService.isAdmin();

        // Then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("프로젝트 상태 접근 권한 검증 - 인증된 사용자가 all 요청")
    void validateProjectStatusAccess_Success_AuthenticatedUserRequestsAll() {
        // Given
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                "admin@example.com",
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        when(securityContext.getAuthentication()).thenReturn(authentication);

        // When
        String result = securityService.validateProjectStatusAccess("all");

        // Then
        assertThat(result).isEqualTo("all");
    }

    @Test
    @DisplayName("프로젝트 상태 접근 권한 검증 - 비인증 사용자가 all 요청")
    void validateProjectStatusAccess_Restricted_UnauthenticatedUserRequestsAll() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        String result = securityService.validateProjectStatusAccess("all");

        // Then
        assertThat(result).isEqualTo("published");
    }

    @Test
    @DisplayName("프로젝트 상태 접근 권한 검증 - 비인증 사용자가 published 요청")
    void validateProjectStatusAccess_Success_UnauthenticatedUserRequestsPublished() {
        // Given
        when(securityContext.getAuthentication()).thenReturn(null);

        // When
        String result = securityService.validateProjectStatusAccess("published");

        // Then
        assertThat(result).isEqualTo("published");
    }
}