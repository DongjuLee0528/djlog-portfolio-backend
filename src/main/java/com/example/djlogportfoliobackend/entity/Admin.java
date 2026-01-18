package com.example.djlogportfoliobackend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 관리자 계정 정보를 관리하는 엔티티
 * 포트폴리오 사이트의 관리자 인증 및 권한 관리를 위한 사용자 정보
 */
@Entity
@Table(name = "admin")
@Getter
@Setter
@NoArgsConstructor
public class Admin {

    /** 관리자 계정의 고유 식별자 */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 관리자 로그인용 이메일 (고유값) */
    @Column(unique = true, nullable = false)
    private String email;

    /** 암호화된 비밀번호 */
    @Column(nullable = false)
    private String password;

    /**
     * Admin 생성자
     * @param email 관리자 이메일
     * @param password 암호화된 비밀번호
     */
    public Admin(String email, String password) {
        this.email = email;
        this.password = password;
    }
}