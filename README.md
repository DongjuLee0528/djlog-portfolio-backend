# djlog-portfolio-backend

개인 포트폴리오 사이트를 위한 Spring Boot 백엔드입니다.

이 프로젝트는 공개 포트폴리오 데이터를 조회하는 API와, 관리자 권한으로 포트폴리오 내용을 수정하는 API를 함께 제공합니다. 프로젝트, 프로필, 학력, 자격증, 기술 스택, 성과/경험, 파일 업로드, 관리자 인증을 하나의 애플리케이션에서 관리합니다.

## 프로젝트 성격

이 백엔드는 단순한 CRUD 서버라기보다 단일 사용자 포트폴리오를 운영하기 위한 관리용 API 서버에 가깝습니다.

- 공개 사용자는 프로젝트, 프로필, 학력, 자격증, 기술 스택을 조회할 수 있습니다.
- 관리자는 JWT 인증 후 포트폴리오 데이터를 생성, 수정, 삭제할 수 있습니다.
- 프로필은 단일 문서처럼 동작하며 `skills`, `education`, `certificates`, `achievements`를 한 번에 교체 저장합니다.
- 프로젝트는 링크, 기술 스택, Q&A, 태그, 게시 상태를 포함한 구조로 관리됩니다.

## 주요 기능

### 1. 관리자 인증

- `POST /api/auth/login`으로 로그인
- `POST /api/auth/logout`으로 로그아웃
- JWT 기반 인증
- Redis 기반 JWT 블랙리스트 및 세션 관리
- 동시 로그인 제한과 로그인 이력 관리

### 2. 프로필 관리

- `GET /api/profile`
- `PUT /api/profile`
- 이름, 직무, 소개, 이메일, GitHub, 이력서 URL 관리
- 기술 스택, 학력, 자격증, 성과/경험을 포함한 전체 프로필 갱신
- 자격증 날짜는 `"2025.05"`, `"2025-05"`, `"2025-05-01"` 형식을 받아 내부적으로 `LocalDate`로 저장

### 3. 프로젝트 관리

- 프로젝트 목록 조회, 단건 조회, 생성, 수정, 삭제
- 게시 상태(`PUBLISHED`, `DRAFT`) 기반 노출 제어
- 카테고리/태그 필터링
- 프로젝트 이미지 업로드
- 프로젝트별 Q&A 목록 조회 및 표시 순서 일괄 수정

### 4. 개별 이력 데이터 관리

- `Education`, `Certificate`, `Skill` 각각 별도 CRUD API 제공
- 프로필 일괄 수정 경로와 개별 관리 경로를 함께 유지

### 5. 파일 업로드

- 이미지 업로드 API 제공
- 확장자, MIME 타입, 파일 크기, 매직 넘버 검증
- UUID 파일명 저장
- `/uploads/**` 경로로 정적 접근 허용

### 6. 보안 및 운영 기능

- Spring Security 기반 엔드포인트 접근 제어
- CORS 설정
- Rate limiting 필터
- 보안 헤더 필터
- 전역 예외 처리
- 환경별 설정 분리 (`dev`, `prod`, `test`)

## 기술 스택

- Java 17
- Spring Boot 3.3.6
- Spring Web
- Spring Data JPA
- Spring Security
- Spring Validation
- Spring Data Redis
- MySQL
- H2 (테스트)
- JWT (`jjwt`)
- Lombok
- Gradle
- Logback + logstash-logback-encoder
- Caffeine

## API 개요

### 인증

```text
POST /api/auth/login
POST /api/auth/logout
```

### 프로필

```text
GET /api/profile
PUT /api/profile
```

### 프로젝트

```text
GET    /api/projects
GET    /api/projects/{id}
GET    /api/projects/{projectId}/qna
POST   /api/projects
PUT    /api/projects/{id}
PUT    /api/projects/{projectId}/qna/display-order
DELETE /api/projects/{id}
POST   /api/projects/image
```

### 학력

```text
GET    /api/educations
POST   /api/educations
PUT    /api/educations/{id}
DELETE /api/educations/{id}
```

### 자격증

```text
GET    /api/certificates
POST   /api/certificates
PUT    /api/certificates/{id}
DELETE /api/certificates/{id}
```

### 기술 스택

```text
GET    /api/skills
POST   /api/skills
PUT    /api/skills/{id}
DELETE /api/skills/{id}
```

### 파일 업로드

```text
POST /api/upload
```

## 접근 정책

현재 보안 설정 기준으로 아래 정책을 가집니다.

- `GET /api/profile` 공개
- `GET /api/projects/**` 공개
- `GET /api/educations/**` 공개
- `GET /api/certificates/**` 공개
- `GET /api/skills/**` 공개
- `/uploads/**` 공개
- 그 외 생성, 수정, 삭제 요청은 인증 필요

## 도메인 구조

핵심 엔티티는 다음과 같습니다.

- `Profile`: 단일 포트폴리오 소유자의 기본 정보
- `Skill`: 프로필에 연결된 기술 스택
- `Education`: 프로필에 연결된 학력
- `Certificate`: 프로필에 연결된 자격증
- `Achievement`: 프로필에 연결된 성과/경험
- `Project`: 포트폴리오 프로젝트
- `ProjectSkill`: 프로젝트 기술 항목
- `ProjectLink`: 프로젝트 외부 링크
- `ProjectQnA`: 프로젝트 상세 설명용 질문/답변
- `Admin`: 관리자 계정

## 현재 코드 기준 특징

### 단일 프로필 모델

프로필은 여러 명의 사용자를 위한 구조가 아니라, 하나의 포트폴리오 운영자를 위한 구조입니다.

- 애플리케이션 시작 시 기본 프로필이 없으면 생성합니다.
- 프로필이 여러 개 생기면 경고 로그를 남기지만, 서비스 로직은 첫 번째 프로필을 기준으로 동작합니다.

### 자격증 날짜 처리

현재 `CertificateRequest.issueDate`는 문자열로 입력을 받고, 저장 직전에 파싱합니다.

- `"2025.05"` -> `2025-05-01`
- `"2025-05"` -> `2025-05-01`
- `"2025-05-01"` -> 그대로 유지

응답에서는 다시 `LocalDate` 기반 JSON 날짜 문자열로 내려가며, 프로필 응답의 자격증 목록은 최신순으로 정렬됩니다.

## 디렉터리 구조

```text
src
├── main
│   ├── java/com/example/djlogportfoliobackend
│   │   ├── config
│   │   ├── controller
│   │   ├── dto
│   │   ├── entity
│   │   ├── exception
│   │   ├── filter
│   │   ├── repository
│   │   ├── service
│   │   └── util
│   └── resources
│       ├── application.properties
│       ├── application-dev.properties
│       └── application-prod.properties
└── test
    ├── java
    │   ├── filter
    │   ├── integration
    │   └── service
    └── resources
```

## 실행 전 준비물

- Java 17
- MySQL
- Redis

개발 환경에서도 Redis 연결 설정이 필요합니다. 테스트는 H2를 사용합니다.

## 환경 변수

최소한 아래 값은 준비하는 것이 좋습니다.

```env
SPRING_PROFILES_ACTIVE=dev

JWT_SECRET=replace-with-a-secure-secret
JWT_EXPIRATION=86400000

DB_URL=jdbc:mysql://localhost:3306/portfolio
DB_USERNAME=root
DB_PASSWORD=password

REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=
REDIS_DATABASE=0

ADMIN_USERNAME=admin@example.com
ADMIN_PASSWORD=change-me

CORS_ALLOWED_ORIGINS=http://localhost:3000
RATE_LIMIT_RPM=60

FILE_UPLOAD_DIR=uploads-dev
```

## 로컬 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=dev'
```

운영 설정으로 실행하려면:

```bash
./gradlew bootRun --args='--spring.profiles.active=prod'
```

## 빌드와 테스트

```bash
./gradlew build
./gradlew test
```

프로필 통합 테스트만 실행하려면:

```bash
./gradlew test --tests com.example.djlogportfoliobackend.integration.ProfileControllerIntegrationTest
```

## 테스트 구성

현재 테스트는 다음 범위를 포함합니다.

- `ProfileControllerIntegrationTest`
  프로필 저장, 하위 컬렉션 교체, 자격증 날짜 변환, 정렬 검증
- `ProjectControllerIntegrationTest`
  프로젝트 API 통합 검증
- `AuthServiceTest`
  인증 로직 검증
- `ProjectServiceTest`
  프로젝트 서비스 검증
- `SecurityServiceTest`
  보안 관련 서비스 검증
- `RateLimitFilterTest`
  요청 제한 필터 검증

## 운영 시 참고 사항

- `prod` 프로필에서는 `spring.jpa.hibernate.ddl-auto=validate`입니다.
- 기본 프로필은 자동 생성될 수 있습니다.
- 관리자 계정도 시작 시 없으면 자동 생성됩니다.
- 업로드 경로는 운영에서 기본적으로 `/var/app/uploads`를 사용합니다.
- 공개 조회 API와 관리자 수정 API가 한 애플리케이션 안에 함께 들어 있으므로, CORS와 JWT 설정을 배포 환경에 맞게 조정해야 합니다.

## 한 줄 요약

이 프로젝트는 개인 포트폴리오 사이트의 공개 조회 API와 관리자용 콘텐츠 관리 API를 함께 제공하는 Spring Boot 백엔드입니다.
