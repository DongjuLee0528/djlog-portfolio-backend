package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.UUID;

/**
 * 기술 정보 응답 DTO 클래스
 *
 * <p>클라이언트에게 기술 스택 정보를 전달하기 위한 데이터 전송 객체입니다.
 * 개인이 보유한 기술의 이름, 카테고리, 숙련도 정보를 포함합니다.</p>
 *
 * <p>주요 사용 위치:</p>
 * <ul>
 *   <li>About 페이지 - Skills 섹션에서 기술 목록 표시</li>
 *   <li>프로필 페이지 - 보유 기술 스택 표시</li>
 *   <li>관리자 페이지 - 기술 관리 및 편집</li>
 *   <li>메인 페이지 - 기술 요약 정보 표시</li>
 * </ul>
 *
 * <p>기술 카테고리 예시:</p>
 * <ul>
 *   <li>Frontend - React, Vue.js, JavaScript 등</li>
 *   <li>Backend - Java, Spring Boot, Node.js 등</li>
 *   <li>Database - MySQL, PostgreSQL, MongoDB 등</li>
 *   <li>DevOps - Docker, Kubernetes, AWS 등</li>
 *   <li>Tools - Git, IntelliJ IDEA, VSCode 등</li>
 * </ul>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
@Data
public class SkillResponse {
    /**
     * 기술 고유 식별자
     */
    private UUID id;

    /**
     * 기술 이름
     */
    private String name;

    /**
     * 기술 카테고리 (예: Frontend, Backend, Database)
     */
    private String category;

    /**
     * 기술 숙련도 (예: Beginner, Intermediate, Advanced)
     */
    private String proficiency;
}