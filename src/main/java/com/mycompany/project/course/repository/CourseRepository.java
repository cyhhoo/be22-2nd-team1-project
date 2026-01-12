package com.mycompany.project.course.repository;

import com.mycompany.project.course.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByAcademicYearId(Long academicYearId);

    // 교사별 강좌 목록 조회
    Page<Course> findByTeacherDetailId(Long teacherDetailId, Pageable pageable);

    // 전체 강좌 목록 조회 (관리자용)
    Page<Course> findAll(Pageable pageable); // JpaRepository 기본 메서드지만 명시적 페이징 지원 확인
}
