package com.example.djlogportfoliobackend;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * DJ Log 포트폴리오 백엔드 애플리케이션의 메인 클래스
 *
 * <p>개인 포트폴리오 웹사이트의 백엔드 REST API 서버를 제공합니다.
 * 프로젝트 관리, 프로필 정보, 관리자 인증 등의 기능을 포함합니다.</p>
 *
 * <p>주요 기능:</p>
 * <ul>
 *   <li>프로젝트 CRUD 및 카테고리/태그 필터링</li>
 *   <li>개인 프로필 정보 관리</li>
 *   <li>JWT 기반 관리자 인증 및 세션 관리</li>
 *   <li>파일 업로드 및 정적 리소스 관리</li>
 *   <li>Redis 기반 캐싱 및 세션 저장</li>
 * </ul>
 *
 * @author DJ
 * @version 1.0
 * @since 2024
 */
@SpringBootApplication
public class DjlogPortfolioBackendApplication {

    /**
     * 애플리케이션의 진입점
     *
     * <p>.env 파일에서 환경 변수를 로드하고 Spring Boot 애플리케이션을 시작합니다.
     * dotenv 라이브러리를 사용하여 개발 환경에서 환경 변수를 쉽게 관리할 수 있습니다.</p>
     *
     * @param args 명령줄 인수 (일반적으로 사용되지 않음)
     */
    public static void main(String[] args) {
        // .env 파일에서 환경 변수를 로드하여 시스템 프로퍼티로 설정
        // ignoreIfMissing()을 통해 .env 파일이 없어도 애플리케이션이 정상 실행되도록 함
        Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));

        // Spring Boot 애플리케이션 시작
        SpringApplication.run(DjlogPortfolioBackendApplication.class, args);
    }

}
