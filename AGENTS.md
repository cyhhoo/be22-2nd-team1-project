# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/com/mycompany/project`: Spring Boot application code organized by domain (e.g., `attendance`, `course`, `schedule`, `user`).
- `src/main/resources`: configuration and mapper files (`application*.yaml`, `logback-spring.xml`, `mapper/**/*.xml`).
- `src/test/java`: JUnit tests (e.g., `SignupIntegrationTest`).
- `build.gradle`, `settings.gradle`, `gradlew`: Gradle build and wrapper.

## Build, Test, and Development Commands
- `./gradlew bootRun`: run the application locally using the default profile.
- `./gradlew test`: run unit/integration tests with JUnit Platform.
- `./gradlew clean build`: clean and build the project (includes tests).
- `./gradlew bootJar`: build the executable Spring Boot jar.

## Coding Style & Naming Conventions
- Language level: Java 17 (Gradle toolchain).
- Packages follow `com.mycompany.project.<domain>`; keep domain boundaries (e.g., `attendance/service`, `attendance/controller`).
- DTOs use `Request/Response` suffixes (e.g., `AttendanceCreateRequest`).
- MyBatis mappers live in `src/main/resources/mapper/**` and have `*Mapper.xml` naming.
- Lombok is used; keep annotations minimal and prefer explicit fields for clarity.

## Testing Guidelines
- Framework: JUnit 5 via `spring-boot-starter-test`.
- Place tests under `src/test/java` and mirror package paths where possible.
- Name tests with `*Test` or `*IntegrationTest` suffixes.
- Use `./gradlew test` for local verification.

## Commit & Pull Request Guidelines
- Recent commit subjects mix prefixes and plain text (e.g., `fix/...`, `refactor/...`, Korean descriptions). Keep subjects short and action-oriented.
- For PRs, include a concise summary, key changes, and any required setup or migration notes.
- Attach API/UI evidence when applicable (screenshots, sample requests, or logs).

## Configuration & Security Tips
- Local DB settings live in `src/main/resources/application.yaml` (MariaDB defaults).
- `src/main/resources/application-dev.yaml` imports `application-local.yaml` if present; store secrets there and avoid committing credentials.
- JWT secret is configured via `jwt.secret` in `application-dev.yaml`; rotate for production.

## Agent Instructions
- Responses are in Korean by default, but every third response should be in Japanese.
