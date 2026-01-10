-- Minimal sample data for attendance HTTP tests (schoolmate DB)
-- Assumes schema aligned with current entities (student_id/teacher_id columns)

INSERT INTO tbl_user (user_id, email, password, name, role, status, birth_date, auth_code, login_fail_count, last_login_at, created_at, updated_at)
VALUES
  (1, 'admin@schoolmate.test', 'password', 'Admin', 'ADMIN', 'ACTIVE', '1980-01-01', NULL, 0, NULL, NOW(), NOW()),
  (10, 'teacher@schoolmate.test', 'password', 'Teacher', 'TEACHER', 'ACTIVE', '1985-03-01', NULL, 0, NULL, NOW(), NOW()),
  (1001, 'student1@schoolmate.test', 'password', 'Student One', 'STUDENT', 'ACTIVE', '2007-03-01', NULL, 0, NULL, NOW(), NOW()),
  (1002, 'student2@schoolmate.test', 'password', 'Student Two', 'STUDENT', 'ACTIVE', '2007-03-02', NULL, 0, NULL, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  email = VALUES(email),
  name = VALUES(name),
  role = VALUES(role),
  status = VALUES(status),
  updated_at = VALUES(updated_at);

INSERT INTO tbl_subject (subject_id, name)
VALUES (1, 'Math')
ON DUPLICATE KEY UPDATE
  name = VALUES(name);

INSERT INTO tbl_academic_year (academic_year_id, year, semester, start_date, end_date, is_current)
VALUES (1, 2025, 1, '2025-03-01', '2025-07-31', 1)
ON DUPLICATE KEY UPDATE
  year = VALUES(year),
  semester = VALUES(semester),
  start_date = VALUES(start_date),
  end_date = VALUES(end_date),
  is_current = VALUES(is_current);

INSERT INTO tbl_teacher_detail (teacher_id, subject_id, homeroom_grade, homeroom_class_no)
VALUES (10, 1, 1, 1)
ON DUPLICATE KEY UPDATE
  subject_id = VALUES(subject_id),
  homeroom_grade = VALUES(homeroom_grade),
  homeroom_class_no = VALUES(homeroom_class_no);

INSERT INTO tbl_student_detail (student_id, student_grade, student_class_no, student_no)
VALUES
  (1001, 1, '1', 1),
  (1002, 1, '1', 2)
ON DUPLICATE KEY UPDATE
  student_grade = VALUES(student_grade),
  student_class_no = VALUES(student_class_no),
  student_no = VALUES(student_no);

INSERT INTO tbl_course (course_id, teacher_detail_id, academic_year_id, subject_id, name, course_type, max_capacity, current_count, tuition, status)
VALUES (2001, 10, 1, 1, 'Math-101', 'MANDATORY', 30, 2, 0, 'OPEN')
ON DUPLICATE KEY UPDATE
  teacher_detail_id = VALUES(teacher_detail_id),
  academic_year_id = VALUES(academic_year_id),
  subject_id = VALUES(subject_id),
  name = VALUES(name),
  course_type = VALUES(course_type),
  max_capacity = VALUES(max_capacity),
  current_count = VALUES(current_count),
  tuition = VALUES(tuition),
  status = VALUES(status);

INSERT INTO tbl_enrollment (enrollment_id, course_id, student_id, status, created_at)
VALUES
  (3001, 2001, 1001, 'APPLIED', NOW()),
  (3002, 2001, 1002, 'APPLIED', NOW())
ON DUPLICATE KEY UPDATE
  course_id = VALUES(course_id),
  student_id = VALUES(student_id),
  status = VALUES(status);

INSERT INTO tbl_attendance_code (attendance_code_id, code, name, is_excused, is_active, created_at, updated_at)
VALUES
  (1, 'PRESENT', 'Present', 0, 1, NOW(), NOW()),
  (2, 'LATE', 'Late', 0, 1, NOW(), NOW()),
  (3, 'ABSENT', 'Absent', 0, 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE
  code = VALUES(code),
  name = VALUES(name),
  is_excused = VALUES(is_excused),
  is_active = VALUES(is_active),
  updated_at = VALUES(updated_at);
