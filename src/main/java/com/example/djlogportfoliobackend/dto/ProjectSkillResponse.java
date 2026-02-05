package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.UUID;

/**
 * 프로젝트에서 사용된 기술 스택 정보 응답 DTO
 *
 * <p>특정 프로젝트에서 사용된 기술 스택 정보를 클라이언트에게 전송하기 위한
 * 데이터 전송 객체입니다. 개인 기술 스택(SkillResponse)과 달리, 특정 프로젝트와
 * 연관된 기술 정보만을 포함합니다.</p>
 *
 * <p>주요 사용 위치:</p>
 * <ul>
 *   <li>프로젝트 상세 페이지 - 해당 프로젝트에서 사용된 기술 목록</li>
 *   <li>프로젝트 카드 - 기술 스택 태그 표시</li>
 *   <li>프로젝트 목록 페이지 - 기술별 필터링</li>
 *   <li>관리자 페이지 - 프로젝트별 기술 관리</li>
 * </ul>
 *
 * <p>SkillResponse와의 차이점:</p>
 * <ul>
 *   <li>숙련도(proficiency) 정보 미포함 - 프로젝트별로는 사용 여부만 중요</li>
 *   <li>특정 프로젝트와 연관성 있는 기술만 표시</li>
 *   <li>프로젝트 컨텍스트에서 기술 활용도 표시 목적</li>
 * </ul>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
@Data
public class ProjectSkillResponse {

    /**
     * 기술 스택 고유 식별자
     */
    private UUID id;

    /**
     * 기술 스택 이름 (예: Java, Spring Boot, React 등)
     */
    private String name;

    /**
     * 기술 카테고리 (예: Backend, Frontend, Database 등)
     */
    private String category;
}