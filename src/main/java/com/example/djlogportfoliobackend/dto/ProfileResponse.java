package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * 프로필 정보 조회 응답 DTO
 *
 * <p>클라이언트에게 프로필 정보를 전송하기 위한 데이터 전송 객체입니다.
 * 개인 정보, 기술 스택, 연락처 정보 등을 포함하여 포트폴리오 웹사이트의
 * 다양한 페이지에서 활용됩니다.</p>
 *
 * <p>주요 사용 위치:</p>
 * <ul>
 *   <li>포트폴리오 메인 페이지 - 프로필 섹션 표시</li>
 *   <li>About 페이지 - 상세 정보 및 자기소개 표시</li>
 *   <li>관리자 페이지 - 프로필 편집 화면 데이터 로드</li>
 *   <li>Contact 섹션 - 연락처 정보 표시</li>
 * </ul>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
@Data
public class ProfileResponse {

    /**
     * 프로필 고유 식별자
     */
    private UUID id;

    /**
     * 이름
     */
    private String name;

    /**
     * 직업/직무
     * 예: Backend Developer, Full Stack Developer
     */
    private String job;

    /**
     * 간단한 자기소개
     * 한 줄 정도의 짧은 소개
     */
    private String bio;

    /**
     * 상세한 자기소개
     * 경력, 관심사, 목표 등을 포함한 긴 소개
     */
    private String about;

    /**
     * 프로필 이미지 URL
     */
    private String image;

    /**
     * 이메일 주소
     */
    private String email;

    /**
     * GitHub 프로필 URL
     */
    private String github;

    /**
     * 이력서/CV URL
     */
    private String resume;

    /**
     * 보유 기술 스택 목록
     */
    private List<SkillResponse> skills;
}