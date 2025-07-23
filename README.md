# Spring Boot Login Form

Java Spring Boot와 HTML5를 사용한 현대적인 로그인/회원가입 시스템입니다.

## 🚀 주요 기능

- **사용자 인증**: Spring Security를 이용한 안전한 로그인/로그아웃
- **회원가입**: 유효성 검사가 포함된 회원가입 기능
- **반응형 디자인**: 모든 디바이스에서 최적화된 사용자 경험
- **비밀번호 암호화**: BCrypt를 사용한 안전한 비밀번호 저장
- **H2 데이터베이스**: 개발용 내장 데이터베이스
- **현대적인 UI**: CSS3와 Font Awesome을 활용한 아름다운 디자인

## 🛠️ 기술 스택

- **Backend**: Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Frontend**: HTML5, CSS3, JavaScript, Thymeleaf
- **Database**: H2 Database (개발용)
- **Build Tool**: Maven
- **Java**: 17+

## 📋 사전 요구사항

- Java 17 이상
- Maven 3.6 이상

## 🚀 실행 방법

1. **저장소 클론**
   ```bash
   git clone <repository-url>
   cd login-form
   ```

2. **애플리케이션 실행**
   ```bash
   mvn spring-boot:run
   ```

3. **브라우저에서 접속**
   ```
   http://localhost:8080
   ```

## 🔐 기본 계정

애플리케이션 시작 시 다음 계정들이 자동으로 생성됩니다:

### 관리자 계정
- **사용자명**: `admin`
- **비밀번호**: `password`

### 일반 사용자 계정
- **사용자명**: `user`
- **비밀번호**: `password`

## 📱 페이지 구성

### 1. 로그인 페이지 (`/login`)
- 사용자명/비밀번호 입력
- 로그인 상태 유지 옵션
- 회원가입 링크
- 에러 메시지 표시

### 2. 회원가입 페이지 (`/register`)
- 사용자명, 이메일, 비밀번호 입력
- 실시간 비밀번호 확인 검증
- 이용약관 동의
- 중복 검사

### 3. 대시보드 페이지 (`/dashboard`)
- 로그인 후 표시되는 메인 페이지
- 사용자 정보 표시
- 로그아웃 기능

## 🗄️ 데이터베이스

### H2 콘솔 접속
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **사용자명**: `sa`
- **비밀번호**: (비어있음)

### 테이블 구조
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(255) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    enabled BOOLEAN DEFAULT TRUE
);
```

## 🔧 설정

### application.yml
주요 설정 파일로 다음 항목들을 포함합니다:
- 서버 포트 (8080)
- H2 데이터베이스 설정
- JPA/Hibernate 설정
- Thymeleaf 템플릿 설정

### 보안 설정
- CSRF 보호 활성화
- 세션 기반 인증
- BCrypt 비밀번호 암호화
- 권한 기반 접근 제어

## 🎨 UI 특징

- **현대적인 디자인**: 그라디언트 배경과 카드 스타일 레이아웃
- **반응형**: 모바일, 태블릿, 데스크톱 최적화
- **아이콘**: Font Awesome 아이콘 사용
- **타이포그래피**: Google Fonts (Poppins) 사용
- **애니메이션**: 부드러운 호버 효과와 전환

## 🔍 주요 파일 구조

```
src/
├── main/
│   ├── java/com/example/loginform/
│   │   ├── LoginFormApplication.java      # 메인 애플리케이션 클래스
│   │   ├── config/
│   │   │   ├── SecurityConfig.java        # Spring Security 설정
│   │   │   └── DataInitializer.java       # 초기 데이터 생성
│   │   ├── controller/
│   │   │   └── AuthController.java        # 인증 관련 컨트롤러
│   │   ├── model/
│   │   │   └── User.java                  # 사용자 엔티티
│   │   ├── repository/
│   │   │   └── UserRepository.java        # 사용자 데이터 접근
│   │   └── service/
│   │       ├── UserService.java           # 사용자 비즈니스 로직
│   │       └── CustomUserDetailsService.java # Spring Security 인증
│   └── resources/
│       ├── application.yml                # 애플리케이션 설정
│       ├── static/css/
│       │   └── style.css                  # 스타일시트
│       └── templates/
│           ├── login.html                 # 로그인 페이지
│           ├── register.html              # 회원가입 페이지
│           └── dashboard.html             # 대시보드 페이지
```

## 🧪 테스트

1. **로그인 테스트**
   - 기본 계정으로 로그인 시도
   - 잘못된 계정 정보로 로그인 시도
   - 로그아웃 테스트

2. **회원가입 테스트**
   - 새 계정 생성
   - 중복 사용자명/이메일 검증
   - 비밀번호 확인 검증

## 📝 개발 참고사항

- 개발 환경에서는 H2 메모리 데이터베이스 사용
- 프로덕션 환경에서는 MySQL/PostgreSQL 등으로 변경 권장
- CSRF 토큰이 자동으로 처리됨
- 세션 기반 인증 사용

## 🔒 보안 고려사항

- 비밀번호는 BCrypt로 해시화되어 저장
- CSRF 공격 방지
- XSS 보호 헤더 포함
- 세션 고정 공격 방지
- 안전한 로그아웃 처리

## 📞 문의

프로젝트에 대한 문의사항이나 개선 제안이 있으시면 이슈를 생성해 주세요.

---

**Spring Boot Login Form** - 안전하고 현대적인 웹 인증 시스템
