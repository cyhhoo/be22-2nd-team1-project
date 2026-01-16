package com.mycompany.project.enrollment.command.domain.repository;

import com.mycompany.project.enrollment.command.domain.aggregate.Enrollment;
import com.mycompany.project.enrollment.query.dto.EnrollmentHistoryResponse;
import com.mycompany.project.enrollment.query.dto.TimetableResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnrollmentMapper {

  /**
   * Inquiry of enrollment history
   */
  List<EnrollmentHistoryResponse> selectHistoryByUserId(@Param("userId") Long userId);

  /**
   * Inquiry of timetable
   */
  List<TimetableResponse> selectTimetableByUserId(@Param("userId") Long userId);

  /**
   * Inquiry of enrollment list for a specific course (Attendance book, etc.)
   */
  List<Enrollment> selectByCourseIdsAndStatus(
      @Param("courseIds") List<Long> courseIds,
      @Param("status") String status);
}