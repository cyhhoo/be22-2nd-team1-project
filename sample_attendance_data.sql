START TRANSACTION;

-- Subject
INSERT INTO tbl_subject (subject_id, name)
VALUES (1001, 'Math');

-- Academic year (entity uses proc_year)
INSERT INTO tbl_academic_year (academic_year_id, proc_year, semester, start_date, end_date, is_current)
VALUES (1001, 2026, 1, '2026-03-01', '2026-07-15', 1);

-- Users (password is bcrypt for Passw0rd!)
INSERT INTO tbl_user (user_id, email, password, name, role, status, birth_date, login_fail_count, created_at, updated_at)
VALUES
  (1001, 'teacher1@schoolmate.local', '$2y$10$HND8tKCP8KcjTUCpcsFGae2Y7LrP2zvu43majP5r1oBhN4csqFRhu', 'Teacher One', 'TEACHER', 'ACTIVE', '1990-01-01', 0, NOW(), NOW()),
  (2001, 'student1@schoolmate.local', '$2y$10$HND8tKCP8KcjTUCpcsFGae2Y7LrP2zvu43majP5r1oBhN4csqFRhu', 'Student One', 'STUDENT', 'ACTIVE', '2008-03-03', 0, NOW(), NOW()),
  (2002, 'student2@schoolmate.local', '$2y$10$HND8tKCP8KcjTUCpcsFGae2Y7LrP2zvu43majP5r1oBhN4csqFRhu', 'Student Two', 'STUDENT', 'ACTIVE', '2008-04-04', 0, NOW(), NOW()),
  (9001, 'admin1@schoolmate.local', '$2y$10$HND8tKCP8KcjTUCpcsFGae2Y7LrP2zvu43majP5r1oBhN4csqFRhu', 'Admin One', 'ADMIN', 'ACTIVE', '1985-03-03', 0, NOW(), NOW());

-- Teacher detail (shared PK with user)
INSERT INTO tbl_teacher_detail (teacher_id, subject_id, homeroom_grade, homeroom_class_no)
VALUES (1001, 1001, 1, 1)
ON DUPLICATE KEY UPDATE
    subject_id = VALUES(subject_id),
    homeroom_grade = VALUES(homeroom_grade),
    homeroom_class_no = VALUES(homeroom_class_no);

-- Student detail (shared PK with user)
INSERT INTO tbl_student_detail (student_id, student_grade, student_class_no, student_no)
VALUES
  (2001, 1, '1', 1),
  (2002, 1, '1', 2);

-- Course
INSERT INTO tbl_course (
  course_id, teacher_detail_id, academic_year_id, subject_id, name, course_type,
  max_capacity, current_count, tuition, status, created_at
)
VALUES (1001, 1001, 1001, 1001, 'Math 1', 'MANDATORY', 30, 0, 0, 'OPEN', NOW());

-- Course time slots
INSERT INTO tbl_course_time_slot (slot_id, course_id, day_of_week, period, classroom)
VALUES
  (1001, 1001, 'MON', 1, '101'),
  (1002, 1001, 'WED', 2, '101');

-- Attendance codes
INSERT INTO tbl_attendance_code (
  attendance_code_id, code, name, is_excused, is_active, created_at, updated_at
)
VALUES
  (1, 'PRESENT', '출석', 0, 1, NOW(), NOW()),
  (2, 'LATE', '지각', 0, 1, NOW(), NOW());

-- Enrollments
INSERT INTO tbl_enrollment (
  enrollment_id, student_detail_id, course_id, status, created_at, updated_at
)
VALUES
  (3001, 2001, 1001, 'APPLIED', NOW(), NOW()),
  (3002, 2002, 1001, 'APPLIED', NOW(), NOW());

COMMIT;
