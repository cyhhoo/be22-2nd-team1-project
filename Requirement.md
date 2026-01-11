# [Project] School Mate: 백엔드 중심의 학사 관리 플랫폼 요구사항 정의서

## 1. 프로젝트 개요
본 프로젝트는 **Spring Boot 3.x**와 **Java 17**을 기반으로 하는 학사 관리 시스템 백엔드 구축 프로젝트입니다.
화면(UI) 개발 없이 API 서버 구축에 집중하며, **데이터 무결성(JPA)**과 **고성능 조회(MyBatis)**를 동시에 달성하는 **이원화된 데이터 접근 전략(Hybrid Data Access Strategy)**을 핵심 아키텍처로 채택합니다.

(중요) 프로젝트 내부의 method는 전부 수동 구현해야함. 프로젝트를 구현하는데 필요한 method의 구조는 잡아주되 로직은 자동으로 작성하지 말고, 가이드만 제공

## 2. 사용 기술 스택
 * Language: Java 17
 * Framework: Spring Boot 3.5.9
 * Build Tool: Gradle
 * Database: MariaDB (Driver: `mariadb-java-client`)
 * Security : Spring Security
    * Authentication : JWT (JSON Web Token) 기반의 Stateless 인증
    * Access Token & Refresh Token 구현
    * Library: `io.jsonwebtoken:jjwt` (또는 유사 라이브러리)
 * Persistence (Hybrid Strategy):
    * Main : MyBatis 3.0.5 (XML-based SQL Mapping)
    * Optional : Spring Data JPA (Basic CRUD & Entity management)
 * API Documentation: Swagger (SpringDoc OpenAPI)

---

## 3. 데이터 접근 전략 (Architecture Rule)
본 프로젝트는 기능의 성격에 따라 데이터 접근 기술을 분리합니다.

1.  **Command (Create, Update, Delete):**
    * **기술:** Spring Data JPA
    * **목적:** 객체 지향적인 도메인 모델링, 데이터 무결성 보장, Dirty Checking 활용.
2.  **Query (Read):**
    * **기술:** MyBatis (XML)
    * **목적:** 복잡한 조인, 동적 쿼리(Dynamic SQL), 통계 및 대량 데이터 조회 성능 최적화.

---

## 4. 핵심 기능 요구사항 (Functional Requirements)

프로젝트는 다음 5가지 핵심 모듈로 구성되며, 각 모듈은 독립적인 기능을 수행하되 유기적으로 연결됩니다.
중요! Domain Driven Design(DDD)을 기반으로 프로젝트를 구현.

### Module 1. 인증 및 사용자 관리 (Auth & User)
* **[통합 로그인 (Login)]**
    * **기본 원칙:** 모든 사용자(교사/학생/학부모/관리자)는 **이메일(Email)** 기반으로 로그인한다.
    * **인증 방식:** Session을 사용하지 않는 **Stateless** 아키텍처를 적용하며, JWT(JSON Web Token)를 발급한다.
    * **보안 정책:**
        * **비밀번호 검증:** BCrypt 해싱 알고리즘을 사용한다.
        * **계정 잠금 (Lock):** 로그인 5회 실패 시 계정을 잠금 처리한다.
    * **응답 규격:** 로그인 성공 시 **Body(JSON)**로 다음 토큰을 반환한다.
        * **Access Token:** 유효기간 30분.
        * **Refresh Token:** 유효기간 7~14일.
* **[계정 상태 관리 및 접근 제어]**
    * **상태 확인:** 로그인 성공 후, DB에 저장된 계정 상태(`status`)를 확인한다.
    * **INACTIVE (미활성) 계정 처리:**
        * Access Token은 정상 발급하되, 서버 측 Filter/Interceptor에서 **계정 활성화(Claim) 화면으로 강제 리다이렉트**를 유도하는 응답을 보낸다 (또는 프론트엔드 처리).
        * **API 접근 제어:** `INACTIVE` 상태인 경우, 계정 활성화 API를 제외한 **모든 일반 서비스 API 접근을 제한(403 Forbidden)**한다.
* **[토큰 재발급 및 보안 (RTR)]**
    * **RTR (Refresh Token Rotation):** 보안 강화를 위해 Refresh Token을 **일회용**으로 처리한다.
    * **로직:** 사용자가 토큰 재발급(Reissue) 요청 시, 기존 Refresh Token을 폐기(Delete/Invalidate)하고 **새로운 Refresh Token을 발급**하여 교체한다. (탈취된 토큰의 재사용 방지)
* **[계정 활성화 (Claim Process)]**
    * **시나리오:** 사용자는 관리자가 미리 생성해둔 계정(`status=INACTIVE`)을 활성화해야 한다.
    * **입력:** `name`, `birthDate`, `authCode` (학생의 생년월일 6자리)
    * **로직:**
        1. DB에서 `name`, `birthDate`, `authCode`가 일치하는 미활성 계정을 조회한다.
        2. 일치 시, 사용자가 입력한 `password`를 BCrypt로 암호화하여 저장한다.
        3. 계정 상태를 `ACTIVE`로 변경한다.


### Module 2. 수강 신청 (Enrollment)
* **[강좌 신청 (Concurrency Control)]**
    * **기술 제약:** Java `synchronized` 키워드 대신 DB 레벨의 Lock을 사용해야 한다.
    * **로직:**
        1. **Pessimistic Lock (비관적 락)**을 사용하여 `Course` 엔티티를 조회한다. (`select ... for update`)
        2. `현재수강인원 < 최대정원` 인지 확인한다. 만약 가득 찼다면 `FullCapacityException`을 발생시킨다.
        3. 이미 신청한 내역이 있는지(`existsByStudentAndCourse`) 확인한다. 있다면 `AlreadyEnrolledException`.
        4. 수강 내역(`Enrollment`)을 저장하고, `Course`의 현재 인원을 +1 증가시킨다(Atomic).
* **[신청 취소]**
    * **로직:** 취소 요청 시 `Enrollment` 데이터를 삭제(Soft Delete 아님, Hard Delete)하고, 반드시 `Course`의 현재 인원을 -1 감소시켜야 한다.

### Module 3. 수강 관리 (Course Management)
* **[강좌 개설 유효성 검사]**
    * **로직:** 강좌 생성 시 다음 두 가지 중복을 검증해야 한다.
        1. **교사 중복:** 해당 교사가 같은 요일/교시에 이미 다른 수업이 있는가?
        2. **강의실 중복:** 해당 강의실이 같은 요일/교시에 이미 예약되어 있는가?
    * **실패 시:** `TimeConflictException` 발생.
* **[시간표 격자 조회 (MyBatis)]**
    * **입력:** `semester`(학기), `userId`
    * **출력:** 요일(Mon~Fri) x 교시(1~8) 형태의 2차원 데이터 구조로 변환하여 반환한다.
    * **쿼리:** `CASE WHEN day = 'MON' AND period = 1 THEN course_name END` 형태의 Pivot 쿼리를 작성한다.

### Module 4. 출결 관리 (Attendance)
* **[교시별 출석부 자동 생성 (Bulk Insert)]**
    * **시나리오:** 교사가 `3월 2일 1교시` 출석부를 처음 조회한다.
    * **로직:**
        1. `Attendance` 테이블에 해당 날짜/교시 데이터가 존재하는지 확인한다.
        2. **없다면(Empty),** 해당 강좌 수강생 전체를 조회하여 기본값(`PRESENT`)으로 `List<Attendance>` 객체를 생성한다.
        3. JPA `saveAll()` 또는 MyBatis `bulkInsert`를 사용하여 DB에 일괄 저장한다.
        4. 저장된 데이터를 반환한다.
* **[출결 마감 (State Machine)]**
    * **로직:** 교사가 '마감' 버튼을 누르면 해당 날짜/교시/과목의 `AttendanceLog`에 마감 상태(`CLOSED`)를 기록한다. 마감된 이후에는 관리자(`ROLE_ADMIN`)만 수정 가능하다.

### Module 5. 시설 예약 (Reservation)
* **[시간 중복 체크 알고리즘]**
    * **입력:** `targetDate`, `startTime`, `endTime`, `teacherId`(또는 `facilityId`)
    * **쿼리 로직:** 기존 예약 테이블에서 아래 조건에 해당하는 레코드가 **0건**이어야 한다.
        ```sql
        WHERE teacher_id = ? 
          AND status != 'CANCELLED'
          AND (
              (start_time < ? AND end_time > ?) -- 요청 시간이 기존 예약 사이에 포함됨
              OR (start_time >= ? AND start_time < ?) -- 요청 시작 시간이 기존 예약과 겹침
          )
        ```
    * **실패 시:** `ReservationOverlapException` 발생.
* **[상태 변경 워크플로우]**
    * `PENDING` (신청) → 교사 승인 → `APPROVED` (확정)
    * `APPROVED` 상태에서는 신청자가 취소할 수 없으며, 교사나 관리자에게 요청해야 한다.


### Module 6. 시스템 초기화 (System Initialization)
* **[초기 계정 대량 등록 (CSV Import)]**
    * **기능:** 시스템 초기 세팅 시 `users.csv` 파일을 읽어 계정을 일괄 생성한다.
    * **포맷:** `이메일`, `이름`, `초기비밀번호`, `권한(ROLE_XXX)`, `생년월일`, `인증코드`
    * **로직:**
        1. CSV 파싱 후 유효성 검사 (이메일 형식, 필수값).
        2. 비밀번호는 BCrypt로 암호화하여 저장.
        3. 중복된 이메일이 존재할 경우 해당 라인은 **Skip**하고, 실패 로그를 남긴다.
* **[초기 학사 데이터 등록 (Bulk Load)]**
    * **기능:** 강의 목록 기초 데이터를 파일(`courses.csv`)로부터 읽어 초기화한다.
    * **포맷:** `강의명`, `교사ID`
    * **로직:**
        1. 참조 데이터(`Teacher`)가 DB에 존재하는지 검증한다.
        2. `Course` 데이터를 생성하여 저장한다. (나머지 상세 정보는 수강 관리 모듈에서 처리)

---

## 5. 예외 처리 및 응답 규격 (Error Handling)
* 모든 API 응답은 `ApiResponse<T>` 래퍼 클래스를 사용한다.
* `GlobalExceptionHandler` (`@RestControllerAdvice`)를 사용하여 아래 예외를 핸들링한다.
    * `BusinessException`: 비즈니스 로직 위반 (400 Bad Request) -> 예: 정원 초과, 중복 신청
    * `EntityNotFoundException`: 리소스 없음 (404 Not Found)
    * `AccessDeniedException`: 권한 부족 (403 Forbidden)
    * `Exception`: 기타 서버 에러 (500 Internal Server Error)


## 6. Schema 정의 (Database Schema)

### 6.1. Module 1. 인증 및 사용자 관리 (Auth & User)

#### 6.1.1 tbl_user (User Table)
* **[tbl_user] (User Table)**
    * `user_id` (PK)
    * `email` (UNIQUE)
    * `password` (BCRYPT)
    * `name`
    * `role` (ENUM)    // ADMIN, TEACHER, STUDENT
    * `status` (ENUM)  // ACTIVE, INACTIVE 초기 데이터 생성시, INACTIVE로 설정
    * `birth_date`    // 생년월일 yyyy-MM-dd 형식
    * `auth_code`     // 초기 데이터 생성시, 생년월일 yyMMdd 형식으로 생성

#### 6.1.2 tbl_student_detail (Student Detail Table)
* **[tbl_student_detail] (Student Detail Table)**
    * `student_detail_id` (PK)
    * `user_id` (FK)  // tbl_user 테이블의 user_id
    * `student_grade` (ENUM)  // 학년 1, 2, 3
    * `student_class_no` (ENUM)  // 반 1, 2, 3, 4, 5
    * `student_no` (UNIQUE) // 번호

#### 6.1.3 tbl_teacher_detail (Teacher Detail Table)
* **[tbl_teacher_detail] (Teacher Detail Table)**
    * `teacher_detail_id` (PK)
    * `user_id` (FK)  // tbl_user 테이블의 user_id
    * `subject_id` (FK)  // tbl_subject 테이블의 subject_id
    * `teacher_grade` (ENUM, Nullable)  // 학년 1, 2, 3
    * `teacher_class_no` (ENUM, Nullable)  // 반 1, 2, 3, 4, 5

#### 6.1.4 tbl_admin_detail (Admin Detail Table)
* **[tbl_admin_detail] (Admin Detail Table)**
    * `admin_detail_id` (PK)
    * `user_id` (FK)  // tbl_user 테이블의 user_id      
    * `level` (ENUM)  // 1, 5 (최고 관리자, 일반 관리자)

#### 6.1.5 tbl_subject (Subject Table)
* **[tbl_subject] (Subject Table)**
    * `subject_id` (PK)
    * `name`

#### 6.1.6 tbl_token (Token Table)
* **[tbl_token] (Token Table)**
    * `token_id` (PK)
    * `user_id` (FK)  // tbl_user 테이블의 user_id
    * `token`   // JWT 토큰

#### 6.1.7 tbl_academic_year (Academic Year Table)
* **[tbl_academic_year] (Academic Year Table)**
    * `academic_year_id` (PK)
    * `year` (UNIQUE)
    * `semester` (ENUM)  // 1, 2

#### 6.1.8 tbl_academic_schedules (Academic Schedule Table)
* **[tbl_academic_schedule] (Academic Schedule Table)**
    * `academic_schedule_id` (PK)
    * `academic_year_id` (FK)  // tbl_academic_year 테이블의 academic_year_id
    * `day` (ENUM)  // MON, TUE, WED, THU, FRI
    * `period` (ENUM)  // 1, 2, 3, 4, 5, 6, 7, 8

#### 6.1.9 tbl_log (Log Table)
* **[tbl_log] (Log Table)**
    * `log_id` (PK)
    * `change_type` (ENUM)  // LOGIN, LOGOUT, CREATE, UPDATE, DELETE, INSERT
    * `modified_at`
    * `modified_by` (FK)  // tbl_user 테이블의 user_id
    * `before_data` (JSON, Nullable)
    * `after_data` (JSON)
    * `table_name` (ENUM)  // 존재하는 테이블명
    * `target_id` (FK)  // 변경된 테이블의 PK

#### 6.1.9 tbl_


### 6.2 Module 2. 수강 신청 (Enrollment)

#### 6.2.1 tbl_enrollment (Enrollment Table)
* **[tbl_enrollment] (Enrollment Table)**
    * `enrollment_id` (PK)
    * `user_id` (FK)  // tbl_user 테이블의 user_id
    * `course_id` (FK)  // tbl_course 테이블의 course_id

### 6.3 Module 3. 수강 관리 (Course Management)

#### 6.3.1 tbl_course (Course Table)
* **[tbl_course] (Course Table)**
    * `course_id` (PK)
    * `name`

### 6.4 Module 4. 출결 관리 (Attendance Management)

### 6.5 Module 5. 시설 예약 (Facility Reservation)

#### 6.5.1 tbl_facility (Facility Table)
* **[tbl_facility] (Facility Table)**
    * `facility_id` (PK)
    * `name`

### 6.6 Module 6. 시스템 초기화 (System Initialization)