package com.mycompany.project.enrollment.query.controller;

import com.mycompany.project.common.response.ApiResponse;
import com.mycompany.project.enrollment.entity.EnrollmentStatus;
import com.mycompany.project.enrollment.query.dto.EnrollmentHistoryResponse;
import com.mycompany.project.enrollment.query.dto.InternalEnrollmentResponse;
import com.mycompany.project.enrollment.query.dto.TimetableResponse;
import com.mycompany.project.enrollment.query.service.EnrollmentQueryService;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "수강 신청 (Enrollment)", description = "수강 신청 조회 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/enrollments")
public class EnrollmentQueryController {

  private final EnrollmentQueryService enrollmentQueryService;

  // 1. 내 수강 내역 조회
  @Operation(summary = "내 수강 내역 목록 조회", description = "내 수강 신청 목록을 조회합니다.")
  @GetMapping("/history")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<ApiResponse<List<EnrollmentHistoryResponse>>> getMyHistory() {
    Long userId = getCurrentUserId();
    List<EnrollmentHistoryResponse> history = enrollmentQueryService.getMyHistory(userId);
    return ResponseEntity.ok(ApiResponse.success(history));
  }

  // 2. 내 시간표 조회
  @Operation(summary = "내 시간표 조회", description = "내 시간표를 조회합니다.")
  @GetMapping("/timetable")
  @PreAuthorize("hasRole('STUDENT')")
  public ResponseEntity<ApiResponse<List<TimetableResponse>>> getMyTimetable() {
    Long userId = getCurrentUserId();
    List<TimetableResponse> timetable = enrollmentQueryService.getMyTimetable(userId);
    return ResponseEntity.ok(ApiResponse.success(timetable));
  }

  private Long getCurrentUserId() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !(authentication.getPrincipal() instanceof CustomUserDetails)) {
      throw new BusinessException(ErrorCode.LOGIN_REQUIRED);
    }

    return ((CustomUserDetails) authentication.getPrincipal()).getUserId();
  }

  // Internal API for other services (Feign)
  @Hidden
  @GetMapping("/internal/course/{courseId}")
  public ResponseEntity<List<InternalEnrollmentResponse>> getInternalEnrollments(
      @PathVariable Long courseId,
      @RequestParam(defaultValue = "APPLIED") EnrollmentStatus status) {
    return ResponseEntity.ok(enrollmentQueryService.getInternalEnrollmentsByCourse(courseId, status));
  }

  @Hidden
  @GetMapping("/internal/{enrollmentId}")
  public ResponseEntity<InternalEnrollmentResponse> getInternalEnrollment(@PathVariable Long enrollmentId) {
    return ResponseEntity.ok(enrollmentQueryService.getInternalEnrollment(enrollmentId));
  }

  @Hidden
  @PostMapping("/internal/enrollments/search")
  public ResponseEntity<List<InternalEnrollmentResponse>> searchEnrollments(
      @RequestBody com.mycompany.project.enrollment.query.dto.EnrollmentSearchRequest request) {
    return ResponseEntity.ok(enrollmentQueryService.searchEnrollments(request));
  }
}
