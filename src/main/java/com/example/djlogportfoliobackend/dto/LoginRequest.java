package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

/**
 * 관리자 로그인 요청을 위한 DTO
 * 클라이언트로부터 로그인 인증 정보를 받기 위한 데이터 전송 객체
 */
@Getter
@Setter
public class LoginRequest {

    /** 로그인용 이메일 주소 */
    @NotBlank(message = "Email is required")
    private String email;

    /** 로그인용 비밀번호 */
    @NotBlank(message = "Password is required")
    private String password;
}