-- #1. 사용자 관리 및 학사 관리, 공통 항목

-- ## 1-1. 사용자 관리
-- ### 1-1-1. 사용자 정보
DROP TABLE IF EXISTS `tbl_user`;
CREATE TABLE `tbl_user` (
	`user_id`	BIGINT	NOT NULL,
	`email`	VARCHAR(100)	NOT NULL	COMMENT '이메일',
	`password`	VARCHAR(255)	NOT NULL	COMMENT 'BCrypt 암호화',
	`name`	VARCHAR(50)	NOT NULL,
	`role`	ENUM('ADMIN', 'TEACHER', 'STUDENT')	NOT NULL	COMMENT 'ENUM: ADMIN, TEACHER, STUDENT',
	`status`	ENUM('ACTIVE', 'INACTIVE', 'LOCKED')	NULL	DEFAULT 'INACTIVE'	COMMENT 'ENUM: ACTIVE, INACTIVE, LOCKED',
	`birth_date`	DATE	NULL	COMMENT '생년월일 (yyyy-MM-dd)',
	`auth_code`	VARCHAR(50)	NULL	COMMENT '초기 생성용 인증코드 (참조용)',
	`login_fail_count`	INT	NULL	DEFAULT 0	COMMENT '로그인 실패 횟수(5회 잠금)',
	`last_login_at`	DATETIME	NULL,
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`updated_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT `PK_TBL_USER` PRIMARY KEY (`user_id`)
);

-- ### 1-1-2. 학생 정보
DROP TABLE IF EXISTS `tbl_student_detail`;
CREATE TABLE `tbl_student_detail` (
	`student_detail_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NULL,
	`student_grade`	INT	NULL	COMMENT '학년 (1, 2, 3)',
	`student_class_no`	INT	NULL	COMMENT '반 (1~10)',
	`student_no`	INT	NULL	COMMENT '출석 번호',
	CONSTRAINT `PK_TBL_STUDENT_DETAIL` PRIMARY KEY (`student_detail_id`)
);

-- ### 1-1-3. 교사 정보
DROP TABLE IF EXISTS `tbl_teacher_detail`;
CREATE TABLE `tbl_teacher_detail` (
	`teacher_detail_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NULL,
	`subject_id`	BIGINT	NULL,
	`homeroom_grade`	INT	NULL	COMMENT '담임 학년 (Nullable)',
	`homeroom_class_no`	INT	NULL	COMMENT '담임 반 (Nullable)',
	CONSTRAINT `PK_TBL_TEACHER_DETAIL` PRIMARY KEY (`teacher_detail_id`)
);

-- ### 1-1-4. 관리자 정보
DROP TABLE IF EXISTS `tbl_admin_detail`;
CREATE TABLE `tbl_admin_detail` (
	`admin_detail_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NULL,
	`level`	ENUM('1', '5')	NOT NULL	DEFAULT '5'	COMMENT 'ENUM: 1(최고), 5(일반)',
	CONSTRAINT `PK_TBL_ADMIN_DETAIL` PRIMARY KEY (`admin_detail_id`)
);

-- ### 1-1-5. 비밀번호 이력
DROP TABLE IF EXISTS `tbl_pwd_history`;
CREATE TABLE `tbl_pwd_history` (
	`history_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NULL,
	`password`	VARCHAR(255)	NOT NULL,
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT `PK_TBL_PWD_HISTORY` PRIMARY KEY (`history_id`)
);

-- ### 1-2. 학사 관리

-- ### 1-2-1. 과목 정보
DROP TABLE IF EXISTS `tbl_subject`;
CREATE TABLE `tbl_subject` (
	`subject_id`	BIGINT	NOT NULL,
	`name`	VARCHAR(50)	NOT NULL	COMMENT '국어, 수학, 영어 등',
	CONSTRAINT `PK_TBL_SUBJECT` PRIMARY KEY (`subject_id`)
);

-- ### 1-2-2. 학기 정보
DROP TABLE IF EXISTS `tbl_academic_year`;
CREATE TABLE `tbl_academic_year` (
	`academic_year_id`	BIGINT	NOT NULL,
	`year`	INT	NOT NULL	COMMENT '2025, 2026...',
	`semester`	INT	NOT NULL	COMMENT '1, 2',
	`start_date`	DATE	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`end_date`	DATE	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`is_current`	BOOLEAN	NULL	DEFAULT FALSE	COMMENT '현재 학기 여부',
	CONSTRAINT `PK_TBL_ACADEMIC_YEAR` PRIMARY KEY (`academic_year_id`)
);

-- ### 1-2-3. 학사 일정
DROP TABLE IF EXISTS `tbl_academic_schedule`;
CREATE TABLE `tbl_academic_schedule` (
	`schedule_id`	BIGINT	NOT NULL,
	`academic_year_id`	BIGINT	NULL,
	`schedule_date`	DATE	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`schedule_type`	ENUM('START', 'EXAM', 'HOLIDAY')	NOT NULL	COMMENT 'ENUM: START, EXAM, HOLIDAY',
	`content`	VARCHAR(255)	NOT NULL,
	`target_grade`	ENUM('1', '2', '3', 'ALL')	NULL	DEFAULT 'ALL'	COMMENT 'ENUM: 1, 2, 3, ALL',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT `PK_TBL_ACADEMIC_SCHEDULE` PRIMARY KEY (`schedule_id`)
);


-- ## 1-3. 공통 항목
-- ### 1-3-1. 로그 내역
DROP TABLE IF EXISTS `tbl_log`;
CREATE TABLE `tbl_log` (
	`log_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NULL	COMMENT '작업자',
	`table_code_id`	INT	NULL,
	`change_type`	ENUM('LOGIN', 'LOGOUT', 'CREATE', 'UPDATE', 'DELETE')	NOT NULL	COMMENT 'ENUM: LOGIN, LOGOUT, CREATE, UPDATE, DELETE',
	`modifed_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '데이터 수정 일시',
	`target_id`	BIGINT	NOT NULL	COMMENT '변경된 Row의 PK',
	`request_id`	VARCHAR(50)	NULL	COMMENT 'UNIQUE, 요청 ID, 로그 추적용,index',
	CONSTRAINT `PK_TBL_LOG` PRIMARY KEY (`log_id`)
);

-- ### 1-3-2. 로그 상세 내역
DROP TABLE IF EXISTS `tbl_log_detail`;
CREATE TABLE `tbl_log_detail` (
	`log_detail_id`	BIGINT	NOT NULL,
	`log_id`	BIGINT	NULL,
	`column_name`	VARCHAR(50)	NOT NULL	COMMENT '변경된 컬럼명 (예: status)',
	`before_value`	TEXT	NULL	COMMENT '변경 전 값 (모든 타입을 문자로 저장)',
	`after_value`	TEXT	NULL	COMMENT '변경 후 값 (모든 타입을 문자로 저장)',
	CONSTRAINT `PK_TBL_LOG_DETAIL` PRIMARY KEY (`log_detail_id`)
);

-- ### 1-3-3. 테이블 코드
DROP TABLE IF EXISTS `tbl_table_code`;
CREATE TABLE `tbl_table_code` (
	`table_code_id`	INT	NOT NULL,
	`table_name`	VARCHAR(50)	NOT NULL	COMMENT '실제 테이블명 (예: tbl_user)',
	`description`	VARCHAR(100)	NULL	COMMENT '테이블 설명 (예: 사용자 기본 정보)',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT `PK_TBL_TABLE_CODE` PRIMARY KEY (`table_code_id`)
);

-- ### 1-3-4. 통합 파일 관리
DROP TABLE IF EXISTS `tbl_file`;
CREATE TABLE `tbl_file` (
	`file_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NULL,
	`original_name`	VARCHAR(255)	NOT NULL,
	`saved_name`	VARCHAR(255)	NOT NULL,
	`saved_path`	VARCHAR(255)	NOT NULL,
	`file_size`	BIGINT	NOT NULL,
	`file_ext`	VARCHAR(10)	NOT NULL,
	`content_type`	VARCHAR(100)	NULL,
	`table_code_id`	INT	NULL	COMMENT '연결된 테이블 ID',
	`related_id`	BIGINT	NOT NULL	COMMENT '연결 대상 PK',
	`is_deleted`	CHAR(1)	NULL	DEFAULT 'N',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT `PK_TBL_FILE` PRIMARY KEY (`file_id`)
);

-- ### 1-3-5. 대량 업로드 로그
DROP TABLE IF EXISTS `tbl_bulk_upload_log`;
CREATE TABLE `tbl_bulk_upload_log` (
	`upload_id`	BIGINT	NOT NULL,
	`file_id`	BIGINT	NULL,
	`admin_detail_id`	BIGINT	NULL,
	`upload_type`	ENUM('STUDENT_REG', 'ATTENDANCE_REG', 'TEACHER_REG')	NOT NULL,
	`status`	ENUM('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED')	NULL	DEFAULT 'PENDING',
	`total_count`	INT	NULL,
	`success_count`	INT	NULL,
	`fail_count`	INT	NULL,
	`error_log`	TEXT	NULL,
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	`finished_at`	DATETIME	NULL	DEFAULT CURRENT_TIMESTAMP,
	`request_id`	VARCHAR(50)	NOT NULL	COMMENT 'UNIQUE, 요청 ID, 로그 추적용,index, tbl_log와 논리적 연결',
	CONSTRAINT `PK_TBL_BULK_UPLOAD_LOG` PRIMARY KEY (`upload_id`)
);

-- ### 1-3-6. 토큰 관리
DROP TABLE IF EXISTS `tbl_token`;
CREATE TABLE `tbl_token` (
	`token_id`	BIGINT	NOT NULL,
	`user_id`	BIGINT	NULL,
	`token_value`	VARCHAR(255)	NOT NULL	COMMENT 'Refresh Token',
	`expiration`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT `PK_TBL_TOKEN` PRIMARY KEY (`token_id`)
);


-- # 2. 수강 신청
-- ### 2-1-1. 수강 신청
DROP TABLE IF EXISTS `tbl_enrollment`;
CREATE TABLE `tbl_enrollment` (
	`enrollment_id`	BIGINT	NOT NULL	COMMENT '수강 신청 PK',
	`course_id`	BIGINT	NOT NULL	COMMENT '수업 코드 FK',
	`student_id`	BIGINT	NOT NULL	COMMENT '학생 user_id FK',
	`status`	ENUM('APPLIED', 'CANCELED', 'FORCED_CANCELED')	NULL	DEFAULT 'APPLIED'	COMMENT 'ENUM: APPLIED, CANCELED, FORCED_CANCELED',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '수강 신청 날짜',
	CONSTRAINT `PK_TBL_ENROLLMENT` PRIMARY KEY (`enrollment_id`)
);

-- ### 2-1-2. 장바구니
DROP TABLE IF EXISTS `tbl_cart`;
CREATE TABLE `tbl_cart` (
	`cart_id`	BIGINT	NOT NULL	COMMENT '장바구니 PK',
	`course_id`	BIGINT	NOT NULL	COMMENT '수업 코드 FK',
	`student_id`	BIGINT	NOT NULL	COMMENT '학생 user_id FK',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '장바구니 담은 날짜',
	CONSTRAINT `PK_TBL_CART` PRIMARY KEY (`cart_id`)
);

-- # 3. 수강 관리
-- ### 3-1-1. 수업 정보
DROP TABLE IF EXISTS `tbl_course`;
CREATE TABLE `tbl_course` (
	`course_id`	BIGINT	NOT NULL,
	`teacher_detail_id`	BIGINT	NULL,
	`academic_year_id`	BIGINT	NULL,
	`subject_id`	BIGINT	NULL,
	`name`	VARCHAR(100)	NOT NULL	COMMENT '강좌명',
	`course_type`	ENUM('MANDATORY', 'ELECTIVE')	NOT NULL	COMMENT 'ENUM: MANDATORY(필수), ELECTIVE(선택)',
	`max_capacity`	INT	NOT NULL	COMMENT '최대 정원',
	`current_count`	INT	NULL	DEFAULT 0	COMMENT '현재 신청 인원',
	`tuition`	INT	NULL	DEFAULT 0	COMMENT '수강료',
	`status`	ENUM('OPEN', 'CLOSED', 'CANCELED')	NULL	DEFAULT 'OPEN'	COMMENT 'ENUM: OPEN, CLOSED, CANCELED',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP,
	CONSTRAINT `PK_TBL_COURSE` PRIMARY KEY (`course_id`)
);
-- ### 3-1-2. 강좌 시간표
DROP TABLE IF EXISTS `tbl_course_time_slot`;
CREATE TABLE `tbl_course_time_slot` (
	`slot_id`	BIGINT	NOT NULL	COMMENT '강좌 시간표 PK',
	`course_id`	BIGINT	NOT NULL	COMMENT '수업 코드 FK',
	`day_of_week`	ENUM('MON', 'TUE', 'WED', 'THU', 'FRI')	NOT NULL	COMMENT 'ENUM: MON, TUE, WED, THU, FRI',
	`period`	INT	NOT NULL	COMMENT 'ENUM: 1~8교시',
	`classroom`	VARCHAR(50)	NOT NULL	COMMENT '강의실명',
	CONSTRAINT `PK_TBL_COURSE_TIME_SLOT` PRIMARY KEY (`slot_id`)
);

-- # 4. 출결 관리
-- ### 4-1-1. 출결
DROP TABLE IF EXISTS `tbl_attendance`;
CREATE TABLE `tbl_attendance` (
	`attendance_id`	BIGINT	NOT NULL	COMMENT 'AUTO_INCREMENT',
	`class_date`	DATE	NOT NULL	COMMENT '수업일자',
	`period`	TINYINT	NOT NULL	COMMENT '교시(1~8)',
	`reason`	VARCHAR(255)	NULL	COMMENT '사유(선택)',
	`state`	ENUM('SAVED', 'CONFIRMED', 'CLOSED')	NOT NULL	DEFAULT 'SAVED'	COMMENT '출결 상태(SAVED/CONFIRMED/CLOSED)',
	`saved_by`	BIGINT	NOT NULL	COMMENT '확정한 교사 user_id(담임/책임교사)',
	`saved_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '저장일시',
	`confirmed_by`	BIGINT	NULL	COMMENT '확정한 교사 user_id(담임/책임교사)',
	`confirmed_at`	DATETIME	NULL	COMMENT '확정일시',
	`closed_at`	DATETIME	NULL	COMMENT '마감일시(선택)',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '생성일시',
	`updated_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '수정일시',
	`attendance_code_id`	BIGINT	NOT NULL	COMMENT '출결코드 FK',
	`enrollment_id`	BIGINT	NOT NULL	COMMENT '수강신청 FK',
	CONSTRAINT `PK_TBL_ATTENDANCE` PRIMARY KEY (`attendance_id`),
	CONSTRAINT `UK_TBL_ATTENDANCE_ENROLLMENT_DATE_PERIOD` UNIQUE (`enrollment_id`, `class_date`, `period`)
);

-- ### 4-1-2. 출결 코드
DROP TABLE IF EXISTS `tbl_attendance_code`;
CREATE TABLE `tbl_attendance_code` (
	`attendance_code_id`	BIGINT	NOT NULL	COMMENT '출결코드 PK',
	`code`	VARCHAR(30)	NOT NULL	COMMENT '코드값 (예: PRESENT, LATE, ABSENT, SICK, OFFICIAL)',
	`name`	VARCHAR(50)	NOT NULL	COMMENT '표시명 (예: 출석, 지각, 결석, 병가, 공가)',
	`is_excused`	TINYINT(1)	NOT NULL	DEFAULT 0	COMMENT '공결/병가 등 참작 여부(1=참작,0=일반)',
	`is_active`	TINYINT(1)	NOT NULL	DEFAULT 1	COMMENT '사용 여부(1=사용,0=비활성)',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '생성일시',
	`updated_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '수정일시',
	CONSTRAINT `PK_TBL_ATTENDANCE_CODE` PRIMARY KEY (`attendance_code_id`)
);

-- ### 4-1-3. 출결 마감
DROP TABLE IF EXISTS `tbl_attendance_closure`;
CREATE TABLE `tbl_attendance_closure` (
	`closure_id`	BIGINT	NOT NULL	COMMENT '마감이력 PK',
	`academic_year_id`	BIGINT	NOT NULL	COMMENT '학년도/학기 FK',
	`scope_type`	ENUM('MONTH', 'SEMESTER')	NOT NULL	COMMENT '마감 범위 타입(MONTH/SEMESTER)',
	`scope_value`	VARCHAR(20)	NOT NULL	COMMENT '범위 값(예: 2025-09 또는 2025-1)',
	`grade`	INT	NULL	COMMENT '학년(선택)',
	`class_no`	INT	NULL	COMMENT '반(선택)',
	`course_id`	BIGINT	NULL	COMMENT '강좌(선택)',
	`closed_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '마감일시',
	`user_id`	BIGINT	NOT NULL	COMMENT '마감관리자 FK',
	CONSTRAINT `PK_TBL_ATTENDANCE_CLOSURE` PRIMARY KEY (`closure_id`)
);

-- ### 4-1-4. 출결 정정 요청
DROP TABLE IF EXISTS `tbl_attendance_correction_request`;
CREATE TABLE `tbl_attendance_correction_request` (
	`request_id`	BIGINT	NOT NULL	AUTO_INCREMENT	COMMENT 'AUTO_INCREMENT 정정요청 PK',
	`before_attendance_code_id`	BIGINT	NOT NULL	COMMENT '변경 전 출결코드 FK',
	`requested_attendance_code_id`	BIGINT	NOT NULL	COMMENT '변경 요청 출결코드 FK',
	`request_reason`	TEXT	NOT NULL	COMMENT '정정 사유(필수)',
	`status`	ENUM('PENDING', 'APPROVED', 'REJECTED')	NOT NULL	DEFAULT 'PENDING'	COMMENT '요청 상태',
	`requested_by`	BIGINT	NOT NULL	COMMENT '요청자(교사) user_id',
	`requested_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '요청일시',
	`decided_by`	BIGINT	NULL	COMMENT '처리자(관리자) user_id',
	`decided_at`	DATETIME	NULL	COMMENT '처리일시',
	`admin_comment`	TEXT	NULL	COMMENT '관리자 코멘트(반려 시 권장)',
	`pending_flag`	TINYINT(1)	NULL	COMMENT 'PENDING 표시용(선택)',
	`attendance_id`	BIGINT	NOT NULL	COMMENT '출결 FK',
	CONSTRAINT `PK_TBL_ATTENDANCE_CORRECTION_REQUEST` PRIMARY KEY (`request_id`)
);


-- # 5. 시설 예약

-- ### 5-1-1. 시설 정보
DROP TABLE IF EXISTS `tbl_facility`;
CREATE TABLE `tbl_facility` (
	`facility_id`	BIGINT	NOT NULL	COMMENT 'AUTO_INCREMENT',
	`admin_detail_id`	BIGINT	NOT NULL	COMMENT '관리자 정보',
	`reservation_id`	BIGINT	NOT NULL	COMMENT 'AUTO_INCREMENT',
	`name`	VARCHAR(50)	NOT NULL	COMMENT '시설명',
	`status`	VARCHAR(20)	NOT NULL	DEFAULT 'AVAILABLE'	COMMENT '예약 가능/불가능',
	CONSTRAINT `PK_TBL_FACILITY` PRIMARY KEY (`facility_id`)
);

-- ### 5-1-2. 시설 예약
DROP TABLE IF EXISTS `tbl_reservation`;
CREATE TABLE `tbl_reservation` (
	`reservation_id`	BIGINT	NOT NULL	COMMENT 'AUTO_INCREMENT',
	`student_detail_id`	BIGINT	NOT NULL	COMMENT '예약 학생 정보',
	`start_time`	TIME	NOT NULL	COMMENT '시작 시간',
	`end_time`	TIME	NOT NULL	COMMENT '종료 시간 (1시간 단위라면 start_time만 있어도 무방)',
	`status`	ENUM('WAITING', 'APPROVED', 'REJECTED', 'CANCELED')	NULL	DEFAULT 'WAITING'	COMMENT '예약상태 ENUM: WAITING, APPROVED, REJECTED, CANCELED',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '예약 신청 시각 (동시 신청 시 우선순위 판단 기준)',
	`rejection_reason`	VARCHAR(255)	NULL	COMMENT '에약 불가 이유',
	CONSTRAINT `PK_TBL_RESERVATION` PRIMARY KEY (`reservation_id`)
);

-- ### 5-1-3. 시설 이용 제한
DROP TABLE IF EXISTS `tbl_facility_restriction`;
CREATE TABLE `tbl_facility_restriction` (
	`restriction_id`	BIGINT	NOT NULL	COMMENT '시설 이용 제한 ID',
	`facility_id`	BIGINT	NOT NULL	COMMENT 'AUTO_INCREMENT',
	`start_date`	DATE	NOT NULL	COMMENT '시설 이용 제한 시작 날짜',
	`end_date`	DATE	NOT NULL	COMMENT '시설 이용 제한 종료 날짜',
	`reason`	VARCHAR(255)	NOT NULL	COMMENT '시설 이용 제한 사유 (공사, 점검 등)',
	`created_at`	DATETIME	NOT NULL	DEFAULT CURRENT_TIMESTAMP	COMMENT '제한 정보 등록 시각',
	CONSTRAINT `PK_TBL_FACILITY_RESTRICTION` PRIMARY KEY (`restriction_id`)
);

ALTER TABLE `tbl_teacher_detail` ADD CONSTRAINT `FK_tbl_user_TO_tbl_teacher_detail_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `tbl_user` (
	`user_id`
);

ALTER TABLE `tbl_teacher_detail` ADD CONSTRAINT `FK_tbl_subject_TO_tbl_teacher_detail_1` FOREIGN KEY (
	`subject_id`
)
REFERENCES `tbl_subject` (
	`subject_id`
);

ALTER TABLE `tbl_attendance_closure` ADD CONSTRAINT `FK_tbl_course_TO_tbl_attendance_closure_1` FOREIGN KEY (
	`course_id`
)
REFERENCES `tbl_course` (
	`course_id`
);

ALTER TABLE `tbl_attendance_closure` ADD CONSTRAINT `FK_tbl_user_TO_tbl_attendance_closure_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `tbl_user` (
	`user_id`
);

ALTER TABLE `tbl_attendance_correction_request` ADD CONSTRAINT `FK_tbl_attendance_TO_tbl_attendance_correction_request_1` FOREIGN KEY (
	`attendance_id`
)
REFERENCES `tbl_attendance` (
	`attendance_id`
);

ALTER TABLE `tbl_facility` ADD CONSTRAINT `FK_tbl_admin_detail_TO_tbl_facility_1` FOREIGN KEY (
	`admin_detail_id`
)
REFERENCES `tbl_admin_detail` (
	`admin_detail_id`
);

ALTER TABLE `tbl_facility` ADD CONSTRAINT `FK_tbl_reservation_TO_tbl_facility_1` FOREIGN KEY (
	`reservation_id`
)
REFERENCES `tbl_reservation` (
	`reservation_id`
);

ALTER TABLE `tbl_student_detail` ADD CONSTRAINT `FK_tbl_user_TO_tbl_student_detail_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `tbl_user` (
	`user_id`
);

ALTER TABLE `tbl_log` ADD CONSTRAINT `FK_tbl_user_TO_tbl_log_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `tbl_user` (
	`user_id`
);

ALTER TABLE `tbl_log` ADD CONSTRAINT `FK_tbl_table_code_TO_tbl_log_1` FOREIGN KEY (
	`table_code_id`
)
REFERENCES `tbl_table_code` (
	`table_code_id`
);

ALTER TABLE `tbl_reservation` ADD CONSTRAINT `FK_tbl_student_detail_TO_tbl_reservation_1` FOREIGN KEY (
	`student_detail_id`
)
REFERENCES `tbl_student_detail` (
	`student_detail_id`
);

ALTER TABLE `tbl_token` ADD CONSTRAINT `FK_tbl_user_TO_tbl_token_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `tbl_user` (
	`user_id`
);

ALTER TABLE `tbl_admin_detail` ADD CONSTRAINT `FK_tbl_user_TO_tbl_admin_detail_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `tbl_user` (
	`user_id`
);

ALTER TABLE `tbl_attendance` ADD CONSTRAINT `FK_tbl_attendance_code_TO_tbl_attendance_1` FOREIGN KEY (
	`attendance_code_id`
)
REFERENCES `tbl_attendance_code` (
	`attendance_code_id`
);

ALTER TABLE `tbl_attendance` ADD CONSTRAINT `FK_tbl_enrollment_TO_tbl_attendance_1` FOREIGN KEY (
	`enrollment_id`
)
REFERENCES `tbl_enrollment` (
	`enrollment_id`
);

ALTER TABLE `tbl_course_time_slot` ADD CONSTRAINT `FK_tbl_course_TO_tbl_course_time_slot_1` FOREIGN KEY (
	`course_id`
)
REFERENCES `tbl_course` (
	`course_id`
);

ALTER TABLE `tbl_log_detail` ADD CONSTRAINT `FK_tbl_log_TO_tbl_log_detail_1` FOREIGN KEY (
	`log_id`
)
REFERENCES `tbl_log` (
	`log_id`
);

ALTER TABLE `tbl_academic_schedule` ADD CONSTRAINT `FK_tbl_academic_year_TO_tbl_academic_schedule_1` FOREIGN KEY (
	`academic_year_id`
)
REFERENCES `tbl_academic_year` (
	`academic_year_id`
);

ALTER TABLE `tbl_enrollment` ADD CONSTRAINT `FK_tbl_course_TO_tbl_enrollment_1` FOREIGN KEY (
	`course_id`
)
REFERENCES `tbl_course` (
	`course_id`
);

ALTER TABLE `tbl_enrollment` ADD CONSTRAINT `FK_tbl_user_TO_tbl_enrollment_1` FOREIGN KEY (
	`student_id`
)
REFERENCES `tbl_user` (
	`user_id`
);

ALTER TABLE `tbl_pwd_history` ADD CONSTRAINT `FK_tbl_user_TO_tbl_pwd_history_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `tbl_user` (
	`user_id`
);

ALTER TABLE `tbl_course` ADD CONSTRAINT `FK_tbl_teacher_detail_TO_tbl_course_1` FOREIGN KEY (
	`teacher_detail_id`
)
REFERENCES `tbl_teacher_detail` (
	`teacher_detail_id`
);

ALTER TABLE `tbl_course` ADD CONSTRAINT `FK_tbl_academic_year_TO_tbl_course_1` FOREIGN KEY (
	`academic_year_id`
)
REFERENCES `tbl_academic_year` (
	`academic_year_id`
);

ALTER TABLE `tbl_course` ADD CONSTRAINT `FK_tbl_subject_TO_tbl_course_1` FOREIGN KEY (
	`subject_id`
)
REFERENCES `tbl_subject` (
	`subject_id`
);

ALTER TABLE `tbl_facility_restriction` ADD CONSTRAINT `FK_tbl_facility_TO_tbl_facility_restriction_1` FOREIGN KEY (
	`facility_id`
)
REFERENCES `tbl_facility` (
	`facility_id`
);

ALTER TABLE `tbl_cart` ADD CONSTRAINT `FK_tbl_course_TO_tbl_cart_1` FOREIGN KEY (
	`course_id`
)
REFERENCES `tbl_course` (
	`course_id`
);

ALTER TABLE `tbl_cart` ADD CONSTRAINT `FK_tbl_user_TO_tbl_cart_1` FOREIGN KEY (
	`student_id`
)
REFERENCES `tbl_user` (
	`user_id`
);

ALTER TABLE `tbl_bulk_upload_log` ADD CONSTRAINT `FK_tbl_file_TO_tbl_bulk_upload_log_1` FOREIGN KEY (
	`file_id`
)
REFERENCES `tbl_file` (
	`file_id`
);

ALTER TABLE `tbl_bulk_upload_log` ADD CONSTRAINT `FK_tbl_admin_detail_TO_tbl_bulk_upload_log_1` FOREIGN KEY (
	`admin_detail_id`
)
REFERENCES `tbl_admin_detail` (
	`admin_detail_id`
);

ALTER TABLE `tbl_file` ADD CONSTRAINT `FK_tbl_user_TO_tbl_file_1` FOREIGN KEY (
	`user_id`
)
REFERENCES `tbl_user` (
	`user_id`
);

ALTER TABLE `tbl_file` ADD CONSTRAINT `FK_tbl_table_code_TO_tbl_file_1` FOREIGN KEY (
	`table_code_id`
)
REFERENCES `tbl_table_code` (
	`table_code_id`
);
