package com.example.djlogportfoliobackend.entity;

/**
 * 프로젝트의 공개 상태를 나타내는 열거형
 *
 * <p>프로젝트가 현재 어떤 상태인지를 정의합니다.
 * 관리자는 프로젝트를 초안 상태로 작성한 후, 완성되면 공개 상태로 변경할 수 있습니다.</p>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
public enum ProjectStatus {

    /**
     * 초안 상태
     * <p>아직 작성 중이거나 검토가 필요한 프로젝트 상태입니다.
     * 이 상태의 프로젝트는 공개 API를 통해 조회되지 않습니다.</p>
     */
    DRAFT("Draft"),

    /**
     * 공개 상태
     * <p>완성되어 포트폴리오에 공개할 준비가 된 프로젝트 상태입니다.
     * 이 상태의 프로젝트만 공개 API를 통해 조회됩니다.</p>
     */
    PUBLISHED("Published");

    /**
     * 상태의 문자열 표현값
     */
    private final String value;

    /**
     * ProjectStatus 생성자
     *
     * @param value 상태의 문자열 표현값
     */
    ProjectStatus(String value) {
        this.value = value;
    }

    /**
     * 상태의 문자열 표현값을 반환합니다.
     *
     * @return 상태의 문자열 값 (예: "Draft", "Published")
     */
    public String getValue() {
        return value;
    }
}