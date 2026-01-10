package com.mycompany.project.enrollment.repository;

import com.mycompany.project.enrollment.query.dto.EnrollmentHistoryResponse;
import com.mycompany.project.enrollment.query.dto.TimetableResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnrollmentMapper {

  // 내 수강 내역 조회 (과목명, 교사명 포함)
  List<EnrollmentHistoryResponse> selectHistoryByStudentId(@Param("studentId") Long studentId);

  // 내 시간표 조회 (요일, 교시, 강의실 포함)
  List<TimetableResponse> selectTimetableByStudentId(@Param("studentId") Long studentId);
}
