-- Minimal schema patch to support attendance HTTP tests
-- Apply to schoolmate DB before sample_attendance_data.sql if tables/columns are missing.

CREATE TABLE IF NOT EXISTS tbl_subject (
  subject_id BIGINT NOT NULL,
  name VARCHAR(50) NOT NULL,
  PRIMARY KEY (subject_id)
);

CREATE TABLE IF NOT EXISTS tbl_academic_year (
  academic_year_id BIGINT NOT NULL,
  year INT NOT NULL,
  semester INT NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  is_current BOOLEAN DEFAULT FALSE,
  PRIMARY KEY (academic_year_id)
);

CREATE TABLE IF NOT EXISTS tbl_teacher_detail (
  teacher_id BIGINT NOT NULL,
  subject_id BIGINT NULL,
  homeroom_grade INT NULL,
  homeroom_class_no INT NULL,
  PRIMARY KEY (teacher_id)
);

CREATE TABLE IF NOT EXISTS tbl_student_detail (
  student_id BIGINT NOT NULL,
  student_grade INT NULL,
  student_class_no VARCHAR(10) NULL,
  student_no INT NULL,
  PRIMARY KEY (student_id)
);

ALTER TABLE tbl_course
  ADD COLUMN teacher_detail_id BIGINT NULL,
  ADD COLUMN academic_year_id BIGINT NULL,
  ADD COLUMN subject_id BIGINT NULL,
  ADD COLUMN name VARCHAR(100) NOT NULL DEFAULT 'Sample Course',
  ADD COLUMN course_type VARCHAR(20) NOT NULL DEFAULT 'MANDATORY',
  ADD COLUMN max_capacity INT NOT NULL DEFAULT 30,
  ADD COLUMN current_count INT NULL DEFAULT 0,
  ADD COLUMN tuition INT NULL DEFAULT 0,
  ADD COLUMN status VARCHAR(20) NULL DEFAULT 'OPEN',
  ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE tbl_enrollment
  ADD COLUMN course_id BIGINT NULL,
  ADD COLUMN student_id BIGINT NULL,
  ADD COLUMN status VARCHAR(20) NULL DEFAULT 'APPLIED',
  ADD COLUMN created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP;
