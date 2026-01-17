package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.UUID;

/**
 * 프로젝트 링크 생성/수정 요청을 위한 DTO
 * 클라이언트로부터 프로젝트 링크 정보를 받기 위한 데이터 전송 객체
 */
@Data
public class ProjectLinkRequest {

    /** 링크가 속할 프로젝트의 ID */
    private UUID projectId;

    /** 링크의 표시 이름 (예: "GitHub", "Demo", "Documentation") */
    @NotBlank(message = "링크 이름은 필수입니다")
    private String label;

    /** 링크 URL 주소 */
    @NotBlank(message = "링크 주소는 필수입니다")
    private String url;

    /** 링크에 대한 추가 설명 (선택사항) */
    private String description;
}