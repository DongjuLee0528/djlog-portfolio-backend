package com.example.djlogportfoliobackend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인 응답 DTO 클래스
 * 사용자 로그인 성공 시 반환되는 정보를 담고 있습니다.
 */
@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {
    /**
     * JWT 액세스 토큰
     * 인증된 사용자의 API 요청에 사용됩니다.
     */
    private String accessToken;
}