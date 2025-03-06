# DevCatch

DevCatch는 다양한 기술 아티클을 관리하고 구독자에게 이메일 알림을 제공하는 Spring Boot 기반 프로젝트입니다.
RSS 피드 파싱, GPT 요약, 이메일 전송, 정기 스케줄링 등 다양한 기능을 통합하여 최신 기술 정보를 자동으로 수집, 요약, 배포하는 시스템을 구현했습니다.

## 목차

- 개요
- 주요 기능
- 프로젝트 구조
- 설정 및 구성
- CI/CD 및 배포
- 이메일 알림
- 스케줄링
- RSS 파싱 및 GPT 연동

## 개요

DevCatch는 다양한 소스의 RSS 피드를 정기적으로 파싱하여 새로 등록된 아티클을 GPT 요약 서비스를 통해 간결하게 요약하고, 이 정보를 모든 구독자에게 이메일로 자동 발송하는 시스템입니다.
회원 가입, 이메일 인증, 구독 취소(회원 탈퇴) 기능을 제공합니다

## 주요 기능

- RSS 피드 파싱: Rome 라이브러리를 활용하여 다양한 소스(당근, 토스, 카카오, 쿠팡 등)의 RSS 피드를 파싱합니다.
- GPT 요약: 외부 GPT 요약 서비스를 통해 아티클 요약을 생성합니다.
- 이메일 알림: Thymeleaf와 Bootstrap을 활용하여 반응형 이메일 템플릿을 구성하고, 구독자에게 자동으로 알림 메일을 발송합니다.
- 정기 스케줄링: Spring Scheduler를 이용하여 RSS 피드 파싱, 아티클 저장, 이메일 전송 등의 작업을 정기적으로 실행합니다.
- 구독 및 탈퇴 관리: 회원 가입, 이메일 인증, 구독 취소(회원 탈퇴) 기능을 제공하여 사용자 관리를 수행합니다.

## 설정 및 구성

- **Java 버전**: Java 17
- **빌드 도구**: Gradle
- **주요 의존성**:
    - Spring Boot Starter Web, Thymeleaf, Starter Mail, Starter Scheduling
    - Rome (RSS 파싱)
    - Jsoup (HTML 파싱 및 크롤링)
- **환경 설정**: <br>
  application.yml 또는 application.properties 파일에 데이터베이스, SMTP 메일, 기타 필요한 설정을 기입합니다.<br>
  (예: 메일 전송을 위한 JavaMailSender 설정, Docker 배포 시 필요한 설정 등)

## CI/CD 및 배포

- **GitHub Actions**: GitHub와 통합된 CI/CD 파이프라인을 구성하여 자동 빌드, 테스트, 배포 작업을 수행합니다.
- **Docker**: Dockerfile을 사용하여 애플리케이션을 컨테이너화하고, EC2 등에서 배포할 수 있도록 준비되어 있습니다.
    - Dockerfile은 OpenJDK 기반 이미지를 사용하여 Java가 설치된 상태로 빌드됩니다.

## 이메일 알림

- **메일 서비스**: MailService를 통해 Thymeleaf 템플릿 기반의 HTML 이메일을 비동기로 전송합니다.
- **스케줄링**
    - **아티클 생성 스케줄러**: ArticleFromRssScheduler를 통해 RSS 피드를 파싱하고 GPT 요약을 통해 새 아티클을 생성하여 DB에 저장합니다.
    - **이메일 알림 스케줄러**: 정기적으로 새로운 아티클 정보를 모든 회원에게 이메일로 전송합니다.
- **Cron 표현식**:<br>
  예) 매일 자정: "0 0 0 * * ?", 매일 오전 8시: "0 0 8 * * ?"

## RSS 파싱 및 GPT 연동

- RSS 파싱: Rome 라이브러리를 사용하여 RSS 피드를 파싱합니다.
  Jsoup을 이용해 HTML 태그를 제거하여 깔끔한 텍스트를 추출합니다.
- GPT 요약: GPT 요약 서비스를 호출하여 각 아티클에 대한 요약 정보를 생성합니다.
- ArticleFactory: RSS 엔트리를 Article 엔티티로 변환하는 역할을 수행합니다.
