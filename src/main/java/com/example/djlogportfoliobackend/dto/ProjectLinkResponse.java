package com.example.djlogportfoliobackend.dto;

import lombok.Data;

import java.util.UUID;

/**
 * 프로젝트 링크 정보 응답을 위한 DTO
 * 클라이언트에게 프로젝트 링크 정보를 전송하기 위한 데이터 전송 객체
 */
@Data
public class ProjectLinkResponse {

    /** 링크의 고유 식별자 */
    private UUID id;

    /** 링크가 속한 프로젝트의 ID */
    private UUID projectId;

    /** 링크의 표시 이름 (예: "GitHub", "Demo", "Documentation") */
    private String label;

    /** 링크 URL 주소 */
    private String url;

    /** 링크에 대한 추가 설명 (선택사항) */
    private String description;
}