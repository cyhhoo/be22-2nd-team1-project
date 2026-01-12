package com.mycompany.project.enrollment.query.controller;

import com.mycompany.project.enrollment.query.dto.EnrollmentHistoryResponse;
import com.mycompany.project.enrollment.query.dto.TimetableResponse;
import com.mycompany.project.enrollment.query.service.EnrollmentQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "수강 신청 (Enrollment)", description = "수강 신청 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/enrollments")
public class EnrollmentQueryController {

  private final EnrollmentQueryService enrollmentQueryService;

  // 1. 내 수강 내역 조회
  @Operation(summary = "내 수강 내역 목록 조회", description = "내 수강 신청 목록을 조회합니다.")
  @GetMapping("/history")
  public ResponseEntity<List<EnrollmentHistoryResponse>> getMyHistory(
      @RequestHeader("X-USER-ID") Long userId
  ) {
    List<EnrollmentHistoryResponse> history = enrollmentQueryService.getMyHistory(userId);
    return ResponseEntity.ok(history);
  }

  // 2. 내 시간표 조회
  @Operation(summary = "내 시간표 조회", description = "내 시간표를 조회합니다.")
  @GetMapping("/timetable")
  public ResponseEntity<List<TimetableResponse>> getMyTimetable(
      @RequestHeader("X-USER-ID") Long userId
  ) {
    List<TimetableResponse> timetable = enrollmentQueryService.getMyTimetable(userId);
    return ResponseEntity.ok(timetable);
  }
}