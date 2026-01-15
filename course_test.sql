-- [1] 스키마 수정 (500 에러 해결용)
ALTER TABLE `tbl_course` 
MODIFY COLUMN `status` ENUM('PENDING', 'OPEN', 'CLOSED', 'RUNNING', 'CANCELED', 'REFUSE') 
NULL DEFAULT 'PENDING' COMMENT 'ENUM: PENDING, OPEN, CLOSED, RUNNING, CANCELED, REFUSE';

-- [2] 샘플 데이터 생성 (API 테스트용)
START TRANSACTION;

-- 2-1. 기초 데이터: 과목 (Subject ID: 10)
INSERT INTO tbl_subject (subject_id, name)
VALUES (10, 'Computer Science')
ON DUPLICATE KEY UPDATE name = VALUES(name);

-- 2-2. 기초 데이터: 학년도 (Academic Year ID: 1)
-- DB 스키마에 따라 year 또는 proc_year 사용 (sample_attendance_data.sql 기준 proc_year 적용)
INSERT INTO tbl_academic_year (academic_year_id, proc_year, semester, start_date, end_date, is_current)
VALUES (1, 2026, 1, '2026-03-01', '2026-07-15', 1)
ON DUPLICATE KEY UPDATE is_current = VALUES(is_current);

-- 2-3. 사용자 및 상세 정보

-- Teacher1 (User ID: 1001)
INSERT INTO tbl_user (user_id, email, password, name, role, status, birth_date, login_fail_count)
VALUES (1001, 'teacher1@schoolmate.local', '$2y$10$HND8tKCP8KcjTUCpcsFGae2Y7LrP2zvu43majP5r1oBhN4csqFRhu', 'Teacher One', 'TEACHER', 'ACTIVE', '1980-01-01', 0)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Teacher Detail (teacher_id 사용)
INSERT INTO tbl_teacher_detail (teacher_id, subject_id, homeroom_grade, homeroom_class_no)
VALUES 
(1001, 10, 1, 1),
(1002, 10, 1, 2)
ON DUPLICATE KEY UPDATE subject_id = VALUES(subject_id);

-- Student50 (User ID: 50)
INSERT INTO tbl_user (user_id, email, password, name, role, status, birth_date, login_fail_count)
VALUES (50, 'student50@schoolmate.local', '$2y$10$HND8tKCP8KcjTUCpcsFGae2Y7LrP2zvu43majP5r1oBhN4csqFRhu', 'Student Fifty', 'STUDENT', 'ACTIVE', '2008-01-01', 0)
ON DUPLICATE KEY UPDATE email = VALUES(email);

-- Student Detail (student_id 사용)
INSERT INTO tbl_student_detail (student_id, student_grade, student_class_no, student_no)
VALUES (50, 1, 1, 50)
ON DUPLICATE KEY UPDATE student_no = VALUES(student_no);

-- 2-4. 강좌 데이터
-- teacher_detail_id 컬럼은 유지 (ERD/Java 일치 추정, sample_data에는 course insert 예시가 teacher_detail_id 사용)
-- Step 136 line 35 of sample_attendance_data.sql uses `teacher_detail_id` inside tbl_course insert! 
-- So tbl_course uses teacher_detail_id, but tbl_teacher_detail uses teacher_id. (Inconsistent naming in DB, but we follow it).
INSERT INTO tbl_course (course_id, teacher_detail_id, academic_year_id, subject_id, name, course_type, max_capacity, tuition, status)
VALUES (1, 1001, 1, 10, 'Intro to Java (Test)', 'MANDATORY', 30, 150000, 'PENDING')
ON DUPLICATE KEY UPDATE status = 'PENDING';

INSERT INTO tbl_course_time_slot (slot_id, course_id, day_of_week, period, classroom)
VALUES 
(101, 1, 'MON', 1, 'Room 101'),
(102, 1, 'WED', 2, 'Room 101')
ON DUPLICATE KEY UPDATE classroom = VALUES(classroom);

INSERT INTO tbl_course (course_id, teacher_detail_id, academic_year_id, subject_id, name, course_type, max_capacity, tuition, status)
VALUES (2, 1001, 1, 10, 'Advanced Java (Test)', 'ELECTIVE', 20, 200000, 'PENDING')
ON DUPLICATE KEY UPDATE status = 'PENDING';

-- 2-5. 수강 신청 데이터
-- tbl_enrollment (student_detail_id 사용)
INSERT INTO tbl_enrollment (enrollment_id, course_id, student_detail_id, status)
VALUES (5001, 1, 50, 'APPLIED')
ON DUPLICATE KEY UPDATE status = 'APPLIED';

COMMIT;
