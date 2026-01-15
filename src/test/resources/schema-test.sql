-- 외래 키 체크 비활성화 (삭제 순서 에러 방지)
SET FOREIGN_KEY_CHECKS = 0;

-- 기존 테이블 삭제
DROP TABLE IF EXISTS tbl_cart;
DROP TABLE IF EXISTS tbl_enrollment;
DROP TABLE IF EXISTS tbl_course_time_slot;
DROP TABLE IF EXISTS tbl_cart;
DROP TABLE IF EXISTS tbl_course;
DROP TABLE IF EXISTS tbl_teacher_detail;
DROP TABLE IF EXISTS tbl_student_detail;
DROP TABLE IF EXISTS tbl_user;

-- 1. 사용자 테이블
CREATE TABLE tbl_user (
                          user_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          email VARCHAR(100) NOT NULL UNIQUE,
                          password VARCHAR(255) NOT NULL,
                          name VARCHAR(50) NOT NULL,
                          birth_date VARCHAR(20) NOT NULL,
                          role VARCHAR(20) NOT NULL,
                          status VARCHAR(20) NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          last_login_at TIMESTAMP NULL,
                          login_fail_count INT DEFAULT 0,
                          auth_code VARCHAR(255)
);

-- 2. 학생 상세
CREATE TABLE tbl_student_detail (
                                    student_id BIGINT PRIMARY KEY,
                                    student_no INT,
                                    student_grade INT,
                                    student_class_no VARCHAR(10),
                                    FOREIGN KEY (student_id) REFERENCES tbl_user(user_id)
);

-- 3. 선생님 상세
CREATE TABLE tbl_teacher_detail (
                                    teacher_id BIGINT PRIMARY KEY,
                                    subject_id BIGINT,
                                    homeroom_grade INT,
                                    homeroom_class_no INT,
                                    FOREIGN KEY (teacher_id) REFERENCES tbl_user(user_id)
);

-- 4. 강좌 테이블
CREATE TABLE tbl_course (
                            course_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                            name VARCHAR(100) NOT NULL,
                            max_capacity INT NOT NULL,
                            current_count INT DEFAULT 0,
                            tuition INT DEFAULT 0,
                            course_type VARCHAR(20) NOT NULL,
                            status VARCHAR(20) DEFAULT 'OPEN',
                            teacher_detail_id BIGINT NOT NULL,
                            academic_year_id BIGINT,
                            subject_id BIGINT,
                            created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (teacher_detail_id) REFERENCES tbl_teacher_detail(teacher_id)
);

-- 5. 강의 시간표
CREATE TABLE tbl_course_time_slot (
                                      slot_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      course_id BIGINT NOT NULL,
                                      day_of_week VARCHAR(10) NOT NULL,
                                      period INT NOT NULL,
                                      classroom VARCHAR(50) NOT NULL,
                                      FOREIGN KEY (course_id) REFERENCES tbl_course(course_id)
);

-- 6. 수강 신청 내역
CREATE TABLE tbl_enrollment (
                                enrollment_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                student_detail_id BIGINT NOT NULL,
                                course_id BIGINT NOT NULL,
                                status VARCHAR(20) NOT NULL,
                                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                FOREIGN KEY (student_detail_id) REFERENCES tbl_student_detail(student_id),
                                FOREIGN KEY (course_id) REFERENCES tbl_course(course_id)
);

-- 7. 장바구니
CREATE TABLE tbl_cart (
                          cart_id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          student_detail_id BIGINT NOT NULL,
                          course_id BIGINT NOT NULL,
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    -- 외래 키 설정
                          FOREIGN KEY (student_detail_id) REFERENCES tbl_student_detail(student_id),
                          FOREIGN KEY (course_id) REFERENCES tbl_course(course_id),

    -- 유니크 제약 조건 (한 학생이 같은 강의를 장바구니에 중복해서 담을 수 없음)
                          UNIQUE KEY uk_cart_student_course (student_detail_id, course_id)
);

-- 외래 키 체크 다시 활성화
SET FOREIGN_KEY_CHECKS = 1;