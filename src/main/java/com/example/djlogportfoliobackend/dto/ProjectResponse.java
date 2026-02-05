package com.example.djlogportfoliobackend.dto;

import com.example.djlogportfoliobackend.entity.ProjectStatus;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * 프로젝트 정보 조회 응답 DTO
 *
 * <p>클라이언트에게 프로젝트 상세 정보를 전송하기 위한 데이터 전송 객체입니다.
 * 프로젝트의 기본 정보, 사용 기술, 관련 링크, Q&A 등 모든 프로젝트 관련 데이터를
 * 포괄적으로 포함합니다.</p>
 *
 * <p>주요 사용 위치:</p>
 * <ul>
 *   <li>포트폴리오 메인 페이지 - 프로젝트 카드 표시</li>
 *   <li>프로젝트 상세 페이지 - 전체 프로젝트 정보 표시</li>
 *   <li>프로젝트 목록 페이지 - 필터링된 프로젝트 리스트 표시</li>
 *   <li>관리자 페이지 - 프로젝트 편집 및 관리</li>
 * </ul>
 *
 * <p>포함되는 데이터:</p>
 * <ul>
 *   <li>프로젝트 기본 정보 (제목, 설명, 카테고리, 상태)</li>
 *   <li>기술 스택 목록</li>
 *   <li>관련 링크 (GitHub, Demo, 문서 등)</li>
 *   <li>Q&A 섹션</li>
 *   <li>메타데이터 (진행기간, 참여기관 등)</li>
 * </ul>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
@Data
public class ProjectResponse {

    /**
     * 프로젝트 고유 식별자
     */
    private UUID id;

    /**
     * 프로젝트 제목
     */
    private String title;

    /**
     * 프로젝트 카테고리 (예: 웹 개발, 모바일 앱 등)
     */
    private String category;

    /**
     * 프로젝트 상태 (진행중, 완료 등)
     */
    private ProjectStatus status;

    /**
     * 프로젝트 상세 설명
     */
    private String description;

    /**
     * 프로젝트 대표 이미지 URL
     */
    private String image;

    /**
     * 프로젝트 태그 목록
     */
    private List<String> tags;

    /**
     * 프로젝트에서 사용한 기술 스택 목록
     */
    private List<ProjectSkillResponse> skills;

    /**
     * 프로젝트 진행 기간
     */
    private String duration;

    /**
     * 참여 회사 또는 기관명
     */
    private String company;

    /**
     * 프로젝트 정렬 순서
     */
    private Integer order;

    /**
     * 프로젝트 관련 링크 목록 (GitHub, 데모 사이트 등)
     */
    private List<ProjectLinkResponse> links;

    /**
     * 프로젝트 Q&A 목록
     */
    private List<ProjectQnAResponse> qnaList;
}