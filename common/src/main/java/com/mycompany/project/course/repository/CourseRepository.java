package com.mycompany.project.course.repository;

import jakarta.persistence.LockModeType;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.course.entity.Course;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

  // [핵심] 비관적 락(Pessimistic Lock) 적용
  // 이 메서드로 조회하면, 트랜잭션이 끝날 때까지 다른 사람은 이 강좌를 수정하지 못하고 대기합니다.
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select c from Course c where c.courseId = :id")
  Optional<Course> findByIdWithLock(@Param("id") Long id);

  // 교사별 강좌 목록 조회
  Page<Course> findByTeacherDetail_Id(Long teacherDetailId, Pageable pageable);

  // 학년도별 강좌 목록 조회
  List<Course> findByAcademicYearId(Long academicYearId);

  // 전체 강좌 목록 조회 (관리자용)
  Page<Course> findAll(Pageable pageable); // JpaRepository 기본 메서드지만 명시적 페이징 지원 확인
}
