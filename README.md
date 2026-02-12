# QR Token 기반 도서 대여 관리 시스템

Spring Boot 기반 QR 도서 대여 시스템입니다.

## 📌 주요 기능

- QR 코드 기반 도서 접근
- Spring Security 기반 인증/인가
- Stateless QR Token 검증 구조
- SHA-256 Secret Key 기반 서명 검증
- Interceptor 선검증 설계
- 관리자/사용자 Role 분리

## 🛠 Tech Stack

- Spring Boot
- Spring Security
- JPA (Hibernate)
- MySQL
- Thymeleaf
- Zxing (QR 생성)
- OpenAI API
- Naver Book API

## 🔐 보안 설계 핵심

- QR URL 위변조 방지
- 토큰 만료 시간 포함
- 서버 서명 검증
- 인증(Authentication)과 접근 무결성(Integrity) 분리 설계

## 🧠 아키텍처 특징

- Controller / Service / Repository 계층 분리
- Token 생성 / 검증 / 요청 차단 책임 분리
- Spring Security Filter Chain 이해 기반 설계

---

백엔드 인증/인가 및 보안 설계를 중심으로 구현한 프로젝트입니다.
