package com.mycompany.project.enrollment.repository;

import com.mycompany.project.enrollment.query.dto.EnrollmentHistoryResponse;
// import com.mycompany.project.enrollment.query.dto.StudentListResponse;
import com.mycompany.project.enrollment.query.dto.TimetableResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface EnrollmentMapper {

  // 1. 내 수강 내역 조회
  List<EnrollmentHistoryResponse> selectHistoryByUserId(@Param("userId") Long userId);

  // 2. 내 시간표 조회
  List<TimetableResponse> selectTimetableByUserId(@Param("userId") Long userId);

  // 3. (출석부용) 특정 강좌의 수강생 목록 조회
  // 교수님이 "내 수업 듣는 학생 누구니?" 할 때 사용
  // List<StudentListResponse> selectStudentsByCourseId(@Param("courseId") Long courseId);
}