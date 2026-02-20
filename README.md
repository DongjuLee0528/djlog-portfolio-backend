# Portfolio Backend

개인 포트폴리오 웹사이트(Backend) API 서버입니다.
프로젝트 관리, 프로필 정보, 파일 업로드, 관리자 인증 기능을 제공합니다.

## Tech Stack
- **Framework**: Spring Boot 3.3.6
- **Language**: Java 17
- **Database**: MySQL + Spring Data JPA
- **Authentication**: JWT + Spring Security
- **Cache**: Redis (세션 관리, JWT 블랙리스트)
- **Build Tool**: Gradle
- **Validation**: Jakarta Validation
- **Security**:
  - JWT 토큰 블랙리스트
  - 타이밍 공격 방지
  - 파일 업로드 보안 (MIME 타입, 매직넘버 검증)
  - CORS 정책
- **Logging**: Logback + Logstash Encoder
- **Environment**: Profile 기반 환경 분리 (dev/prod)

## Features
- **인증/인가**
  - JWT 기반 관리자 로그인/로그아웃
  - 토큰 블랙리스트 관리
  - 세션 관리 (Redis)
  - 타이밍 공격 방지

- **프로젝트 관리**
  - 프로젝트 CRUD 작업
  - 카테고리/태그별 필터링
  - 게시 상태 관리 (published/draft)
  - 프로젝트 링크 및 Q&A 관리

- **프로필 관리**
  - 개인 프로필 정보 조회/수정
  - 학력 정보 CRUD
  - 자격증 정보 CRUD
  - 기술/스킬 정보 CRUD

- **파일 업로드**
  - 이미지 파일 업로드
  - MIME 타입 및 매직넘버 검증
  - 파일 크기 제한
  - 경로 순회 공격 방지

- **보안 기능**
  - 글로벌 예외 처리
  - 보안 헤더 설정
  - 환경별 설정 분리
  - 상세한 보안 로깅

## API Endpoints

### 인증 (Authentication)
```
POST /api/auth/login    # 관리자 로그인
POST /api/auth/logout   # 관리자 로그아웃 (JWT 토큰 무효화)
```

### 프로젝트 (Projects)
```
GET    /api/projects              # 프로젝트 목록 조회 (필터링 지원)
GET    /api/projects/{id}         # 특정 프로젝트 상세 조회
POST   /api/projects              # 새 프로젝트 생성 (인증 필요)
PUT    /api/projects/{id}         # 프로젝트 수정 (인증 필요)
DELETE /api/projects/{id}         # 프로젝트 삭제 (인증 필요)
```

### 프로필 (Profile)
```
GET /api/profile                  # 프로필 정보 조회
PUT /api/profile                  # 프로필 정보 수정 (인증 필요)
```

### 파일 업로드 (File Upload)
```
POST /api/upload                  # 파일 업로드 (인증 필요)
```

### 학력 (Education)
```
GET    /api/educations            # 학력 목록 조회
POST   /api/educations            # 새 학력 추가 (인증 필요)
PUT    /api/educations/{id}       # 학력 수정 (인증 필요)
DELETE /api/educations/{id}       # 학력 삭제 (인증 필요)
```

### 자격증 (Certificate)
```
GET    /api/certificates          # 자격증 목록 조회
POST   /api/certificates          # 새 자격증 추가 (인증 필요)
PUT    /api/certificates/{id}     # 자격증 수정 (인증 필요)
DELETE /api/certificates/{id}     # 자격증 삭제 (인증 필요)
```

### 기술/스킬 (Skills)
```
GET    /api/skills                # 기술/스킬 목록 조회
POST   /api/skills                # 새 기술/스킬 추가 (인증 필요)
PUT    /api/skills/{id}           # 기술/스킬 수정 (인증 필요)
DELETE /api/skills/{id}           # 기술/스킬 삭제 (인증 필요)
```

## Project Structure
```
src/
  main/
    java/com/example/djlogportfoliobackend/
      config/           # 설정 클래스 (Security, Environment, DataLoader, Rate Limit 등)
      controller/       # REST API 컨트롤러 (Auth, Project, Profile, File, Education, Certificate, Skill)
      dto/             # 데이터 전송 객체 (Request/Response DTOs)
      entity/          # JPA 엔티티 (Admin, Project, Profile, Education, Certificate, Skill 등)
      exception/       # 커스텀 예외 및 글로벌 예외 처리
      filter/          # JWT 인증 필터, 요청 로깅 필터, Rate Limit 필터
      repository/      # JPA 리포지토리
      service/         # 비즈니스 로직 서비스 (JWT 블랙리스트, 세션 관리, 파일 업로드 등)
      util/           # 유틸리티 클래스 (JWT 등)
    resources/
      application.properties           # 기본 설정
      application-dev.properties       # 개발 환경 설정
      application-prod.properties      # 운영 환경 설정
  test/
    java/           # 단위 테스트
    resources/      # 테스트 리소스
```

## Security Features
- **JWT 토큰 관리**: Redis 기반 블랙리스트로 로그아웃 시 토큰 무효화
- **세션 관리**: 동시 로그인 제한 및 세션 추적
- **파일 업로드 보안**: MIME 타입, 매직넘버, 파일 크기 검증
- **타이밍 공격 방지**: 사용자 존재 여부와 관계없이 동일한 처리 시간 보장
- **CORS 정책**: 환경별 허용 도메인 설정
- **보안 헤더**: X-Content-Type-Options, X-Frame-Options, CSP 등
- **상세한 로깅**: 보안 이벤트 및 에러 추적

## Environment Configuration
- **개발환경 (dev)**: 상세한 로깅, SQL 쿼리 로깅 활성화
- **운영환경 (prod)**: 최적화된 로깅, 보안 강화 설정
- **프로필별 설정**: 데이터베이스, Redis, CORS 등 환경별 분리

## Getting Started

### Prerequisites
- Java 17+
- MySQL 8.0+
- Redis 6.0+
- Gradle 7.0+

### Installation
1. 저장소 클론
```bash
git clone <repository-url>
cd djlog-portfolio-backend
```

2. 환경 변수 설정 (.env 파일 생성)
```
# 데이터베이스 설정
DB_URL=jdbc:mysql://localhost:3306/portfolio
DB_USERNAME=your_username
DB_PASSWORD=your_password

# JWT 토큰 설정
JWT_SECRET=your_jwt_secret_key_at_least_32_characters_long

# Redis 설정
REDIS_HOST=localhost
REDIS_PORT=6379
REDIS_PASSWORD=your_redis_password
REDIS_DATABASE=0

# 관리자 계정 설정 (초기 관리자 생성용)
ADMIN_USERNAME=admin@example.com
ADMIN_PASSWORD=your_admin_password

# 보안 설정
CORS_ALLOWED_ORIGINS=https://yourdomain.com,https://www.yourdomain.com
RATE_LIMIT_RPM=60

# 파일 업로드 설정
FILE_UPLOAD_DIR=uploads

# Spring 프로파일 설정
SPRING_PROFILES_ACTIVE=dev
```

3. 데이터베이스 스키마 생성
```sql
CREATE DATABASE portfolio CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

4. 빌드 및 실행
```bash
# 개발 환경
./gradlew bootRun --args='--spring.profiles.active=dev'

# 운영 환경
./gradlew bootRun --args='--spring.profiles.active=prod'
```

### API Testing
- Base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html` (개발 환경)
- API 테스트: Postman 등의 도구를 사용하여 각 엔드포인트 테스트

## Build & Deploy
```bash
# JAR 빌드
./gradlew build

# 테스트 실행
./gradlew test

# 도커 이미지 빌드 (Dockerfile 있는 경우)
docker build -t portfolio-backend .
```
