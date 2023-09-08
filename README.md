# Klip Membership Tool Backend

NFT 홀더를 위한 카카오 오픈 채팅을 개설하고 관리하는 기능 제공.
구독 모델 기반의 유료 멤버십 관리까지가 1차 목표.

## Local Development

```shell
./gradlew clean bootRun
```

Or IDE Run: IntelliJ Keymap(⌃⌥R)

### Local 개발을 위한 hosts 설정

```
127.0.0.1	membership-api.local.klipwallet.com membership.local.klipwallet.com
127.0.0.1	membership-admin-api.local.klipwallet.com membership-admin.local.klipwallet.com
```

| Origin                                     | Description                       |
|--------------------------------------------|-----------------------------------|
| http://membership.local.klipwallet.com:3000           | Klip Membership Tool Front@local  |
| http://membership-api.local.klipwallet.com:8080       | Klip Membership Tool API@local    |
| http://membership-admin.local.klipwallet.com:3000     | Klip Membership Admin Front@local |
| http://membership-admin-api.local.klipwallet.com:8080 | Klip Membership Admin API@local   |

> Hosts 변조 유틸리티 for macOS
> * [Gas Mask](https://github.com/2ndalpha/gasmask)
> * [SwitchHosts](https://switchhosts.vercel.app/)

### Local 개발을 위한 추가 속성 설정

기본으로 local 환경이 활성화 되어 있음. (`spring.profiles.default=local`)

`spring.profiles.active=local`

Local에서 개발할 시 Vault 정보를 아래 파일에 속성으로 정의해둬야함

`/src/main/resources/local.properties`

```properties
kakao_admin_key=????????????????????????????????
application.kakao-api.domain-id=???
bgms_pw=????????????????????????????????????????
spring.cloud.vault.enabled=false
spring.datasource.url=jdbc:h2:mem:local;MODE=MYSQL;DATABASE_TO_LOWER=TRUE;
spring.security.oauth2.client.registration.google.client-id=???????????????????????????????????????????????????????????????????
spring.security.oauth2.client.registration.google.client-secret=??????????????????????????????????
kakao_rest_api_key=?????????????????????????????????
```

> 참고: https://vault.dev.klaytn.com/ (1Password)

### Local에서 Dev 환경을 참조하는 개발을 위한 추가 속성 설정

`spring.profiles.active=local-dev`

Local 개발 시 DB, Redis, Kafka 등 인프라를 dev 환경에 의존함.

**`~/.vault-token` 파일 설정이 필수로 요구됨.** 파일 안에 Vault Token 문자열이 존재해야함.

> 참고: https://vault.dev.klaytn.com/ (1Password)

### Swagger-UI

* local: http://localhost:8080/swagger-ui
  * http://membership-api.local.com:8080
  * http://membership-admin-api.local.com:8080
* dev
  * [https://membership-api.dev.klipwallet.com/swagger-ui](http://membership-api.klipwallet.com/swagger-ui)
  * [https://membership-admin-api.dev.klipwallet.com/swagger-ui](https://membership-admin-api.dev.klipwallet.com/swagger-ui)
    * 현재 방화벽 설정으로 인해 접근 안됨

## Test

```shell
# 단위 테스트
./gradlew clean test
# 통합 테스트(현재 미존재)
./gradlew clean integrationTest
# Test All + jacoco(coverage) + Checkstyle + Sonarqube
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

`src/main/java/com.klipwallet.membership`

| package      | description               |
|--------------|---------------------------|
| `adaptor`    | 구현 기술 Adaptors            |
| `config`     | Spring 구성                 |
| `controller` | API                       |
| `dto`        | Data Transfer Object      |
| `entity`     | Entity 등 주요 도메인 모델        |
| `exception`  | Exceptions                |
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

* `PartnerApplication`: 파트너 신청(이용자 등록 요청)
* `Partner`: 파트너(이용자)
* `Operator`: 채팅방 운영진
* `Admin`: 관리자(GX담당자)
* `OpenChatting`: 채팅방
* `OpenChattingMember`: 채팅방 참여자
* `OpenChattingNft`: 채팅방에 입장할 수 있는 NFT 정보
* `KakaoService`: 카카오 연동 서비스
* `KlipAccountService`: Klip 계정 조회 서비스
* `KlipDropService`: KlipDrops 연동 서비스
* `Notice`: 공지사항
* `Faq`: FAQ

# 협업

## Coding Convention(설정 필수!)

https://groundx.atlassian.net/wiki/spaces/KLP/pages/3121086474/BE+Coding+Convention

## DB Naming Convention

https://groundx.atlassian.net/wiki/spaces/KLP/pages/3121184769/DB+Naming+Convention

## 정적 분석

소스 코드 정적 분석 도구

- 코드 결함, 자원 누수, 보안 이슈 등 체크

