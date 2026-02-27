package com.example.djlogportfoliobackend.service;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * 보안 관련 비즈니스 로직 처리 서비스
 * 인증 상태 확인, 권한 검증 등의 기능을 제공합니다.
 */
@Service
public class SecurityService {

    /**
     * 현재 사용자의 인증 상태 확인
     *
     * @return 인증된 사용자인 경우 true, 그렇지 않으면 false
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
            && authentication.isAuthenticated()
            && !(authentication instanceof AnonymousAuthenticationToken);
    }

    /**
     * 현재 사용자의 사용자명 반환
     *
     * @return 인증된 사용자명, 인증되지 않은 경우 null
     */
    public String getCurrentUsername() {
        if (isAuthenticated()) {
            return SecurityContextHolder.getContext().getAuthentication().getName();
        }
        return null;
    }

    /**
     * 관리자 권한 확인
     * 현재는 단순히 인증된 사용자인지만 확인하지만,
     * 향후 역할 기반 권한 시스템으로 확장 가능
     *
     * @return 관리자 권한이 있는 경우 true, 그렇지 않으면 false
     */
    public boolean isAdmin() {
        return isAuthenticated();
    }

    /**
     * 프로젝트 상태 접근 권한 검증
     * 비인증 사용자는 공개된 프로젝트만 볼 수 있도록 제한
     *
     * @param requestedStatus 요청된 프로젝트 상태
     * @return 실제 적용할 프로젝트 상태
     */
    public String validateProjectStatusAccess(String requestedStatus) {
        // 비인증 사용자가 'all'을 요청하면 강제로 'published'로 변경
        if ("all".equalsIgnoreCase(requestedStatus) && !isAuthenticated()) {
            return "published";
        }
        return requestedStatus;
    }
}