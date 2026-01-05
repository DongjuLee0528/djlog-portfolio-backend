package com.example.djlogportfoliobackend.config;

import com.example.djlogportfoliobackend.entity.Admin;
import com.example.djlogportfoliobackend.entity.Profile;
import com.example.djlogportfoliobackend.repository.AdminRepository;
import com.example.djlogportfoliobackend.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 초기 데이터 로더
 * Spring Boot 애플리케이션 시작 시 필요한 초기 데이터를 생성하는 컴포넌트
 *
 * @author 이동주
 * @since 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataLoader implements ApplicationRunner {

    private final AdminRepository adminRepository;
    private final ProfileRepository profileRepository;
    private final PasswordEncoder passwordEncoder;

    // application.properties 또는 application.yml에서 관리자 계정 정보를 주입
    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    /**
     * 애플리케이션 시작 후 실행되는 메소드
     * ApplicationRunner 인터페이스의 구현 메소드로, Spring Boot 애플리케이션이
     * 완전히 시작된 후 자동으로 호출됨
     *
     * @param args 애플리케이션 실행 시 전달된 인자
     */
    @Override
    public void run(ApplicationArguments args) {
        createAdminIfNotExists();
        createDefaultProfileIfNotExists();
    }

    /**
     * 관리자 계정 생성 메소드
     * 데이터베이스에 관리자 계정이 존재하지 않을 경우에만 새로운 관리자 계정을 생성
     * 이미 존재하는 경우 생성을 건너뛰어 중복 생성을 방지
     */
    private void createAdminIfNotExists() {
        // 설정 파일에서 읽어온 username으로 기존 관리자 계정 존재 여부 확인
        if (adminRepository.findByUsername(adminUsername).isEmpty()) {
            // 평문 비밀번호를 암호화하여 저장 (보안을 위한 필수 작업)
            String encodedPassword = passwordEncoder.encode(adminPassword);

            // 새로운 관리자 엔티티 생성
            Admin admin = new Admin(adminUsername, encodedPassword);

            // 데이터베이스에 저장
            adminRepository.save(admin);

            // 로그 출력: 관리자 계정 생성 완료
            log.info("[DATA_LOADER] Default admin user created: {}", adminUsername);
        } else {
            // 로그 출력: 이미 관리자 계정이 존재함
            log.info("[DATA_LOADER] Admin user already exists: {}", adminUsername);
        }
    }

    /**
     * 기본 프로필 생성 메소드
     * 데이터베이스에 프로필이 존재하지 않을 경우에만 기본 프로필을 생성
     */
    private void createDefaultProfileIfNotExists() {
        if (profileRepository.count() == 0) {
            Profile defaultProfile = new Profile(
                    "동주 이",
                    "풀스택 개발자",
                    "안녕하세요! 백엔드와 프론트엔드 개발에 관심이 많은 개발자입니다.\n\n" +
                    "Spring Boot, React, Vue.js 등의 기술을 활용하여 웹 애플리케이션을 개발합니다.",
                    "/images/profile.jpg",
                    "dongju.dev@example.com",
                    "https://github.com/dongjulee"
            );

            profileRepository.save(defaultProfile);
            log.info("[DATA_LOADER] Default profile created for: {}", defaultProfile.getName());
        } else {
            log.info("[DATA_LOADER] Profile data already exists");
        }
    }
}