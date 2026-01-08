package com.mycompany.project.course.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.course.entity.Course;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface CourseRepository extends JpaRepository<Course, Long> {

    // 교사 시간표 중복 체크 (CANCELED, REFUSE 상태 제외)
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Course c " +
            "JOIN c.timeSlots ts " +
            "WHERE c.academicYearId = :academicYearId " +
            "AND c.teacherDetailId = :teacherDetailId " +
            "AND ts.dayOfWeek = :dayOfWeek " +
            "AND ts.period = :period " +
            "AND c.status NOT IN ('CANCELED', 'REFUSE')")
    boolean existsByTeacherAndSchedule(@Param("academicYearId") Long academicYearId,
            @Param("teacherDetailId") Long teacherDetailId,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("period") Integer period);

    // 강의실 시간표 중복 체크 (CANCELED, REFUSE 상태 제외)
    // 강의실은 물리적 공간이므로 교사가 달라도 겹칠 수 없음
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END " +
            "FROM Course c " +
            "JOIN c.timeSlots ts " +
            "WHERE c.academicYearId = :academicYearId " +
            "AND ts.classroom = :classroom " +
            "AND ts.dayOfWeek = :dayOfWeek " +
            "AND ts.period = :period " +
            "AND c.status NOT IN ('CANCELED', 'REFUSE')")
    boolean existsByClassroomAndSchedule(@Param("academicYearId") Long academicYearId,
            @Param("classroom") String classroom,
            @Param("dayOfWeek") String dayOfWeek,
            @Param("period") Integer period);
}
