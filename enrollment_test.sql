-- [기본 설정] 학생(ID:20), 선생님(ID:10)
INSERT INTO tbl_user (user_id, email, password, name, birth_date, role, status) VALUES (10, 'teacher@test.com', '1234', '김선생', '1980-01-01', 'TEACHER', 'ACTIVE');
INSERT INTO tbl_teacher_detail (teacher_id) VALUES (10);

INSERT INTO tbl_user (user_id, email, password, name, birth_date, role, status) VALUES (20, 'student@test.com', '1234', '이학생', '2000-01-01', 'STUDENT', 'ACTIVE');
INSERT INTO tbl_student_detail (student_id, student_grade) VALUES (20, 1);

-- [시나리오 1용] 정상 과목 2개
INSERT INTO tbl_course (course_id, name, max_capacity, current_count, tuition, course_type, status, teacher_detail_id) VALUES (101, '자료구조', 30, 0, 50000, 'MANDATORY', 'OPEN', 10);
INSERT INTO tbl_course (course_id, name, max_capacity, current_count, tuition, course_type, status, teacher_detail_id) VALUES (102, '운영체제', 30, 0, 50000, 'MANDATORY', 'OPEN', 10);

-- [시나리오 2용] 만석 과목 1개, 여유 과목 1개
INSERT INTO tbl_course (course_id, name, max_capacity, current_count, tuition, course_type, status, teacher_detail_id) VALUES (201, '인기교양(만석)', 10, 10, 30000, 'ELECTIVE', 'OPEN', 10);
INSERT INTO tbl_course (course_id, name, max_capacity, current_count, tuition, course_type, status, teacher_detail_id) VALUES (202, '일반교양(여유)', 30, 5, 30000, 'ELECTIVE', 'OPEN', 10);

-- [시나리오 3용] 이미 수강 중인 과목 (재신청 테스트용)
INSERT INTO tbl_course (course_id, name, max_capacity, current_count, tuition, course_type, status, teacher_detail_id) VALUES (301, '이미듣는수업', 30, 1, 50000, 'MANDATORY', 'OPEN', 10);
INSERT INTO tbl_enrollment (student_detail_id, course_id, status) VALUES (20, 301, 'APPLIED');

-- [시간표 데이터] (조회 테스트용)
INSERT INTO tbl_course_time_slot (course_id, day_of_week, period, classroom) VALUES (101, 'MON', 1, 'A101');
INSERT INTO tbl_course_time_slot (course_id, day_of_week, period, classroom) VALUES (102, 'TUE', 2, 'B202');
INSERT INTO tbl_course_time_slot (course_id, day_of_week, period, classroom) VALUES (202, 'TUE', 3, 'B202');
INSERT INTO tbl_course_time_slot (course_id, day_of_week, period, classroom) VALUES (301, 'WED', 3, 'C303');