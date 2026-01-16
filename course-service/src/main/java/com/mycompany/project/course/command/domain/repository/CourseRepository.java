package com.mycompany.project.course.command.domain.repository;

import jakarta.persistence.LockModeType;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.course.command.domain.aggregate.Course;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {

  // [?듭떖] 鍮꾧?????Pessimistic Lock) ?곸슜
  // ??硫붿꽌?쒕줈 議고쉶?섎㈃, ?몃옖??뀡???앸궇 ?뚭퉴吏 ?ㅻⅨ ?щ엺? ??媛뺤쥖瑜??섏젙?섏? 紐삵븯怨??湲고빀?덈떎.
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("select c from Course c where c.courseId = :id")
  Optional<Course> findByIdWithLock(@Param("id") Long id);

  // 援먯궗蹂?媛뺤쥖 紐⑸줉 議고쉶
  Page<Course> findByTeacherDetailId(Long teacherDetailId, Pageable pageable);

  // ?숇뀈?꾨퀎 媛뺤쥖 紐⑸줉 議고쉶
  List<Course> findByAcademicYearId(Long academicYearId);

  // ?꾩껜 媛뺤쥖 紐⑸줉 議고쉶 (愿由ъ옄??
  Page<Course> findAll(Pageable pageable); // JpaRepository 湲곕낯 硫붿꽌?쒖?留?紐낆떆???섏씠吏?吏???뺤씤
}
