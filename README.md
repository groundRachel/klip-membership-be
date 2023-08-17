# Klip Membership Tool Backend

NFT 홀더를 위한 카카오 오픈 채팅을 개설하고 관리하는 기능 제공.
구독 모델 기반의 유료 멤버십 관리까지가 1차 목표.

## Local Development

```shell
./gradlew clean bootRun
```

Or IDE Run: IntelliJ Keymap(⌃⌥R)

### Swagger-UI

[http://localhost:8080/swagger-ui](http://localhost:8080/swagger-ui)

## Test

```shell
# 단위 테스트
./gradlew clean test
# 통합 테스트
./gradlew clean integrationTest
# 둘 다
./gradlew clean check
```

## Build

```shell
# jar 패키징만
./gradlew clean assemble
# 전체 빌드(+Containerizing)
./gradlew clean build jib
```

## Directory Structure

Base
on [Maven Standard Directory Layout](https://maven.apache.org/guides/introduction/introduction-to-the-standard-directory-layout.html)

```
├── build: Gradle 빌드 결과물
│   ├── libs: Artifact(*.jar)
├── config: 정적분석 등 각종 빌드 설정 파일
├── gradle.wrapper: `gradlew`와 속성 
└── src: Source
    ├── main: Production Source
    │   ├── java: Production Java Source
    │   └── resources: Production Resouce(xml, yml, json, media, ...)
    │       └── db.migration: Flyway Sql
    └── test: Test Source
        ├── java: Test Java Source
        ├── groovy: Test Groovy Source(for Spock)
        └── resources: Test Resource(xml, yml, json, media, ...)
            └── application*.yml: Spring Boot 구성 파일
```

## Package Structure

`src/main/java/com.klipwallet.membershiptool`

| package      | description               |
|--------------|---------------------------|
| `adaptor`    | 구현 기술 Component           |
| `config`     | Spring 구성                 |
| `controller` | Web Mvc Controller(API)   |
| `entity`     | Entity 등 주요 도메인 모델        |
| `repository` | Repository 등 영속화 컴포넌트     |
| `service`    | Rich Service(Transaction) |

> 최신 내용은 각 패키지의 `package-info.java` 참조 요망

## Tech Stack

* Java 17
* Spring Boot 3.x
* Spring Web MVC
* Spring Security
  * oauth2-client(google)
* Spring Data JPA
  * Hibernate, Flyway
* Spring Cloud
  * OpenFeign, CircuitBreaker, Vault
* Spring Session, Cache
  * Spring Data Redis, Lettuce

> [HELP.md](./HELP.md) 참고 요망

### Build Tools

* Gradle 8
* Jib

### Test

* Junit5
* Spock On Groovy
* Mockito
* AssertJ

### AWS Infra

* EKS: K8S
  * By Helm Chart
* Aurora MySQL
  * case insensitive identifiers
* ElastiCache for Redis: Session, Cache
* S3(ObjectStorage): `media.klipwallet.com`
* MSK(Kafka): Subscribe KAS Notification

> By Terraform

## Architecture

- Layered Architecture
- [Transaction Script](https://martinfowler.com/eaaCatalog/transactionScript.html)? Domain Model?

## 주요 모델

* `Member`: 파트너(이용자)
* `MemberApplication`: 파트너 신청(이용자 등록 요청)
* `Admin`: 관리자(GX담당자)
* `ChatRoom`: 채팅방
* `ChatRoomMember`: 채팅방 참여자(방장/부방장 포함?)
* `KakaoService`: 카카오 연동 서비스

# 협업

## Coding Convention(설정 필수!)

https://groundx.atlassian.net/wiki/spaces/KLP/pages/3121086474/BE+Coding+Convention

## DB Naming Convention

https://groundx.atlassian.net/wiki/spaces/KLP/pages/3121184769/DB+Naming+Convention

## 정적 분석

소스 코드 정적 분석 도구

- 코드 결함, 자원 누수, 보안 이슈 등 체크

