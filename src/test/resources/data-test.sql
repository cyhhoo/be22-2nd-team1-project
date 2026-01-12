-- 1. 선생님 계정 (ID: 10)
INSERT INTO tbl_user (user_id, email, password, name, birth_date, role, status)
VALUES (10, 'teacher@test.com', '1234', '김선생', '1980-01-01', 'TEACHER', 'ACTIVE');

INSERT INTO tbl_teacher_detail (teacher_id) VALUES (10);

-- 2. 학생 계정 (ID: 20)
INSERT INTO tbl_user (user_id, email, password, name, birth_date, role, status)
VALUES (20, 'student@test.com', '1234', '이학생', '2000-01-01', 'STUDENT', 'ACTIVE');

INSERT INTO tbl_student_detail (student_id, student_grade) VALUES (20, 1);

-- 3. 강좌 생성 (ID: 100)
INSERT INTO tbl_course (course_id, name, max_capacity, current_count, tuition, course_type, status, teacher_detail_id)
VALUES (100, '자바 프로그래밍', 30, 1, 50000, 'MANDATORY', 'OPEN', 10);

-- 4. 시간표 (월1, 수3)
INSERT INTO tbl_course_time_slot (course_id, day_of_week, period, classroom) VALUES (100, 'MON', 1, 'A101');
INSERT INTO tbl_course_time_slot (course_id, day_of_week, period, classroom) VALUES (100, 'WED', 3, 'B202');

-- 5. 수강 신청 완료 내역
INSERT INTO tbl_enrollment (student_detail_id, course_id, status)
VALUES (20, 100, 'APPLIED');

-- [추가] 1. 테스트용 추가 강의 생성 (Course ID: 101 - 알고리즘)
INSERT INTO tbl_course (course_id, name, max_capacity, current_count, tuition, course_type, status, teacher_detail_id)
VALUES (101, '알고리즘', 30, 0, 50000, 'ELECTIVE', 'OPEN', 10);

-- [추가] 2. 강의 101의 시간표 등록 (금요일 5교시)
INSERT INTO tbl_course_time_slot (course_id, day_of_week, period, classroom)
VALUES (101, 'FRI', 5, 'C303');

-- [추가] 3. 학생(20)이 강의(101)를 장바구니에 담음
INSERT INTO tbl_cart (student_detail_id, course_id)
VALUES (20, 101);