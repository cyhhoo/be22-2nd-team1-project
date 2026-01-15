package com.mycompany.project.course.controller;

import com.mycompany.project.course.dto.CourseCreateReqDTO;
import com.mycompany.project.course.dto.CourseUpdateReqDTO;
import com.mycompany.project.course.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mycompany.project.course.dto.StudentDetailResDTO;
import com.mycompany.project.common.response.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "수강 관리 (Course Management)", description = "강좌 개설, 조회 및 시간표 관련 API")
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "강좌 개설 신청", description = "새로운 강좌를 개설 신청합니다. (상태: PENDING)")
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> createCourse(
            @RequestBody @Valid CourseCreateReqDTO dto) {
        courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    @Operation(summary = "강좌 승인", description = "신청된 강좌를 승인하여 개설을 확정합니다. (PENDING -> OPEN)")
    @PostMapping("/{courseId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveCourse(@PathVariable Long courseId) {
        courseService.approveCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "강좌 반려", description = "신청된 강좌를 반려합니다. (PENDING -> REFUSE)")
    @PostMapping("/{courseId}/refuse")
    public ResponseEntity<ApiResponse<Void>> refuseCourse(@PathVariable Long courseId, @RequestParam String reason) {
        courseService.refuseCourse(courseId, reason);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "강좌 변경 요청", description = "운영 중인 강좌의 내용을 변경 요청합니다. (관리자 승인 필요)")
    @PostMapping("/{courseId}/request-update")
    public ResponseEntity<ApiResponse<Long>> requestCourseUpdate(
            @PathVariable Long courseId,
            @RequestBody @Valid CourseUpdateReqDTO dto,
            @RequestParam String reason) {
        Long requestId = courseService.requestCourseUpdate(courseId, dto, reason);
        return ResponseEntity.ok(ApiResponse.success(requestId));
    }

    @Operation(summary = "강좌 변경 요청 승인", description = "대기 중인 변경 요청을 승인하여 강좌 정보에 반영합니다.")
    @PostMapping("/requests/{requestId}/approve")
    public ResponseEntity<ApiResponse<Void>> approveChangeRequest(@PathVariable Long requestId) {
        courseService.approveChangeRequest(requestId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "강좌 변경 요청 반려", description = "대기 중인 변경 요청을 반려합니다.")
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<ApiResponse<Void>> rejectChangeRequest(@PathVariable Long requestId,
            @RequestParam String reason) {
        courseService.rejectChangeRequest(requestId, reason);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "담당 교사 변경", description = "강좌의 담당 교사를 변경합니다. (시간표 중복 확인 포함)")
    @PostMapping("/{courseId}/change-teacher")
    public ResponseEntity<ApiResponse<Void>> changeTeacher(@PathVariable Long courseId,
            @RequestParam Long newTeacherId) {
        courseService.changeTeacher(courseId, newTeacherId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "강좌 삭제 (DeleteCourse)", description = "운영 중인 강좌를 폐강(삭제) 처리합니다. (상태: CANCELED, 수강생 일괄 취소)")
    @PostMapping("/{courseId}/cancel")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long courseId, @RequestParam String reason) {
        courseService.deleteCourse(courseId, reason);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "수강생 상세 조회", description = "수강생의 출결 및 과제 현황, 메모를 조회합니다.")
    @GetMapping("/{courseId}/students/{studentId}")
    public ResponseEntity<ApiResponse<StudentDetailResDTO>> getStudentDetail(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getStudentDetail(courseId, studentId)));
    }

    @Operation(summary = "수강생 강제 취소", description = "특정 수강생의 수강 신청을 강제로 취소합니다. (사유 필수)")
    @PostMapping("/{courseId}/students/{studentId}/force-cancel")
    public ResponseEntity<ApiResponse<Void>> forceCancelStudent(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @RequestParam String reason) {
        courseService.forceCancelStudent(courseId, studentId, reason);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "수강생 메모 수정", description = "수강생에 대한 특이사항 메모를 수정합니다.")
    @PutMapping("/{courseId}/students/{studentId}/memo")
    public ResponseEntity<ApiResponse<Void>> updateStudentMemo(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @RequestBody String memo) {
        courseService.updateStudentMemo(courseId, studentId, memo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "교사별 강좌 목록 조회", description = "특정 교사가 담당하는 강좌 목록을 페이징하여 조회합니다.")
    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<com.mycompany.project.course.dto.CourseListResDTO>>> getCourseList(
            @PathVariable Long teacherId,
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseList(teacherId, pageable)));
    }

    @Operation(summary = "전체 강좌 목록 조회 (관리자)", description = "전체 강좌 목록을 페이징하여 조회합니다. (관리자용)")
    @GetMapping
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<com.mycompany.project.course.dto.CourseListResDTO>>> getAllCourses(
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getAllCourses(pageable)));
    }

    @Operation(summary = "강좌 상태 변경", description = "강좌의 상태를 수동으로 변경합니다. (예: 조기 마감, 재오픈)")
    @PutMapping("/{courseId}/status")
    public ResponseEntity<ApiResponse<Void>> changeCourseStatus(
            @PathVariable Long courseId,
            @RequestParam com.mycompany.project.course.entity.CourseStatus status) {
        courseService.changeCourseStatus(courseId, status);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "교사 주간 시간표 조회", description = "교사의 주간 수업 일정(요일/교시별)을 조회합니다.")
    @GetMapping("/teacher/{teacherId}/timetable")
    public ResponseEntity<ApiResponse<com.mycompany.project.course.dto.TeacherTimetableResDTO>> getTeacherTimetable(
            @PathVariable Long teacherId,
            @RequestParam(defaultValue = "1") Long semester) { // Default semester should be handled properly in real
                                                               // app
        // semester 파라미터는 academicYearId로 가정 (실제로는 학기 서비스에서 현재 학기 ID를 가져와야 함)
        return ResponseEntity.ok(ApiResponse.success(courseService.getTeacherTimetable(teacherId, semester)));
    }
}
