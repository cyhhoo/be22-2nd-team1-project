-- 학년(Academic Year) 데이터 (학기 정보 포함 가정)
INSERT IGNORE INTO tbl_academic_year (academic_year_id, name, year, semester) VALUES (1, '2024년 1학기', 2024, 1);

-- 과목(Subject) 데이터
INSERT IGNORE INTO tbl_subject (subject_id, name) VALUES (10, '컴퓨터 사이언스');
INSERT IGNORE INTO tbl_subject (subject_id, name) VALUES (20, '수학');
INSERT IGNORE INTO tbl_subject (subject_id, name) VALUES (50, '영어');

-- 교사 상세(Teacher Detail) 데이터
-- 주의: tbl_user와 별도로 존재하는 테이블로 가정 (ID 200 매핑)
-- 컬럼이 불확실하므로, 에러 발생 시 테이블 스키마 확인 필요
INSERT IGNORE INTO tbl_teacher_detail (teacher_detail_id, name) VALUES (200, 'Teacher Kim');
