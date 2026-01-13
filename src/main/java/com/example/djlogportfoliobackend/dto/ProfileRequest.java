package com.example.djlogportfoliobackend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.util.List;

/**
 * 프로필 정보 수정 요청을 위한 DTO(Data Transfer Object)
 *
 * <p>관리자가 포트폴리오의 개인 프로필 정보를 수정할 때 사용되는 요청 데이터 클래스입니다.
 * Spring Validation을 통해 입력 데이터의 유효성을 검증합니다.</p>
 *
 * <p>프로필은 포트폴리오 웹사이트의 메인 페이지나 About 페이지에 표시되는
 * 개인 정보를 관리하기 위해 사용됩니다.</p>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
@Data
public class ProfileRequest {

    /**
     * 이름 (필수)
     * <p>포트폴리오 소유자의 이름입니다. 웹사이트 전체에서 표시되는 기본 이름입니다.</p>
     */
    @NotBlank(message = "이름은 필수입니다")
    private String name;

    /**
     * 직업 (선택)
     * <p>현재 직업이나 포지션입니다.
     * 메인 페이지나 프로필 섹션에서 표시됩니다.</p>
     */
    private String job;

    /**
     * 간단한 소개 (선택)
     * <p>한 줄 정도의 짧은 자기소개 문구입니다.
     * 메인 페이지나 헤더 영역에서 간단히 소개할 때 사용됩니다.</p>
     */
    private String bio;

    /**
     * 상세한 자기소개 (선택)
     * <p>About 페이지에서 사용되는 상세한 자기소개 내용입니다.
     * 마크다운 형식을 지원하여 풍부한 텍스트 표현이 가능합니다.</p>
     */
    private String about;

    /**
     * 프로필 이미지 URL (선택)
     * <p>프로필 사진의 URL입니다. 파일 업로드를 통해 저장된 이미지나
     * 외부 이미지 링크를 사용할 수 있습니다.</p>
     */
    private String image;

    /**
     * 이메일 주소 (선택, 이메일 형식 검증)
     * <p>연락처로 사용되는 이메일 주소입니다.
     * Contact 페이지나 연락 정보 섹션에서 표시됩니다.</p>
     */
    @Email(message = "올바른 이메일 형식이어야 합니다")
    private String email;

    /**
     * GitHub 프로필 URL (선택)
     * <p>GitHub 프로필 링크입니다. 포트폴리오에서 소셜 링크나
     * 프로젝트 소스코드 링크로 활용됩니다.</p>
     */
    private String github;

    /**
     * 기술 스택 목록 (선택)
     * <p>보유하고 있는 기술 스택들의 목록입니다.
     * About 페이지나 Skills 섹션에서 표시됩니다.</p>
     */
    private List<SkillRequest> skills;

    /**
     * 이력서 파일 URL (선택)
     * <p>이력서 PDF 파일의 URL입니다.
     * 파일 업로드를 통해 저장된 이력서 링크입니다.</p>
     */
    private String resume;
}