# Spring Cloud Config Server 사용 가이드

## 📋 개요

Spring Cloud Config Server를 통해 모든 마이크로서비스의 설정을 중앙에서 관리합니다.

## 🏗️ 구조

```
config-service/
├── src/main/resources/
│   ├── application.yml          # Config Server 자체 설정
│   └── config/                  # 각 서비스 설정 파일들
│       ├── application.yml      # 공통 설정 (DB, Eureka, JWT)
│       ├── auth-service.yml
│       ├── user-service.yml
│       ├── gateway-service.yml
│       ├── schedule-service.yml
│       ├── attendance-service.yml
│       ├── course-service.yml
│       ├── enrollment-service.yml
│       └── reservation-service.yml
```

## 🚀 서비스 실행 순서

**반드시 아래 순서대로 실행하세요:**

1. **Discovery Service** (Port 8761)
   ```bash
   cd discovery-service
   ../gradlew bootRun
   ```

2. **Config Service** (Port 8888)
   ```bash
   cd config-service
   ../gradlew bootRun
   ```

3. **Gateway Service** (Port 8000)
   - Config Server에서 설정을 가져옴
   
4. **기타 서비스들**(Auth, User, Schedule 등)
   - Config Server에서 설정을 가져옴

## 📝 Config Server 설정 요약

### application.yml (공통 설정)
```yaml
# 모든 서비스에 공통으로 적용되는 설정
spring:
  datasource:
    url: jdbc:mariadb://localhost:3306/schoolmate
    username: swcamp
    password: swcamp
    driver-class-name: org.mariadb.jdbc.Driver

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

jwt:
  secret: your-very-long-secret-key...
```

### 각 서비스별 설정
- **auth-service.yml**: Port 8081
- **user-service.yml**: Port 8082
- **schedule-service.yml**: Port 8083
- **attendance-service.yml**: Port 8084
- **course-service.yml**: Port 8085
- **enrollment-service.yml**: Port 8086
- **reservation-service.yml**: Port 8087
- **gateway-service.yml**: Port 8000 (모든 라우팅 설정 포함)

## 🔧 Config Server 작동 방식

1. **Config Server 시작**: Port 8888에서 실행
2. **설정 위치**: `classpath:/config` (내부 리소스)
3. **설정 로딩**: 각 서비스가 시작할 때 Config Server에서 설정을 가져옴
4. **우선순위**:
   - `{service-name}.yml` (서비스별 설정)
   - `application.yml` (공통 설정)

## 📍 Config Server API

설정 확인 URL:
- 공통 설정: `http://localhost:8888/application/default`
- Auth 설정: `http://localhost:8888/auth-service/default`
- User 설정: `http://localhost:8888/user-service/default`
- Gateway 설정: `http://localhost:8888/gateway-service/default`

## ⚙️ 다음 단계: 각 서비스를 Config Client로 변경

각 서비스에 다음 작업이 필요합니다:

### 1. build.gradle에 의존성 추가
```gradle
dependencies {
    implementation 'org.springframework.cloud:spring-cloud-starter-config'
    // ... 기존 의존성
}
```

### 2. bootstrap.yml 생성
각 서비스의 `src/main/resources/bootstrap.yml`:
```yaml
spring:
  application:
    name: {service-name}  # auth-service, user-service 등
  cloud:
    config:
      uri: http://localhost:8888
      fail-fast: true
```

### 3. application.yml 간소화
기존 application.yml은 삭제하거나 최소한의 설정만 남김

## 🎯 장점

✅ **중앙 집중식 관리**: 모든 설정을 한 곳에서 관리
✅ **설정 공유**: 공통 설정(DB, Eureka, JWT)을 여러 서비스에서 재사용
✅ **동적 변경**: 서비스 재시작 없이 설정 변경 가능 (Refresh 기능 추가 시)
✅ **환경별 설정**: dev, prod 등 프로파일별 설정 가능
✅ **보안**: 민감한 정보를 Git이 아닌 외부 저장소에 보관 가능

## 🔍 트러블슈팅

### Config Server에 연결 안 됨
```
- Config Server가 먼저 실행되었는지 확인
- Port 8888이 사용 가능한지 확인
- bootstrap.yml의 config.uri가 올바른지 확인
```

### 설정을 못 찾음
```
- 서비스명(spring.application.name)이 일치하는지 확인
- config/ 폴더에 해당 서비스 yml 파일이 있는지 확인
```

## 📚 참고

- Config Server Port: **8888**
- Discovery Service Port: **8761**
- Gateway Port: **8000**
- 설정 파일 위치: `config-service/src/main/resources/config/`
