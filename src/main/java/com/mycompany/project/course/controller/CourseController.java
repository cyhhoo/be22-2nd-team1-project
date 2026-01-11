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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "수강 관리 (Course Management)", description = "강좌 개설, 조회 및 시간표 관련 API")
@RestController
@RequestMapping("/api/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "강좌 개설 신청", description = "새로운 강좌를 개설 신청합니다. (상태: PENDING)")
    @PostMapping
    public ResponseEntity<Void> createCourse(@RequestBody @Valid CourseCreateReqDTO dto) {
        courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "강좌 승인", description = "신청된 강좌를 승인하여 개설을 확정합니다. (PENDING -> OPEN)")
    @PostMapping("/{courseId}/approve")
    public ResponseEntity<Void> approveCourse(@PathVariable Long courseId) {
        courseService.approveCourse(courseId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강좌 반려", description = "신청된 강좌를 반려합니다. (PENDING -> REFUSE)")
    @PostMapping("/{courseId}/refuse")
    public ResponseEntity<Void> refuseCourse(@PathVariable Long courseId, @RequestParam String reason) {
        courseService.refuseCourse(courseId, reason);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강좌 변경 요청", description = "운영 중인 강좌의 내용을 변경 요청합니다. (관리자 승인 필요)")
    @PostMapping("/{courseId}/request-update")
    public ResponseEntity<Long> requestCourseUpdate(
            @PathVariable Long courseId,
            @RequestBody @Valid CourseUpdateReqDTO dto,
            @RequestParam String reason) {
        Long requestId = courseService.requestCourseUpdate(courseId, dto, reason);
        return ResponseEntity.ok(requestId);
    }

    @Operation(summary = "강좌 변경 요청 승인", description = "대기 중인 변경 요청을 승인하여 강좌 정보에 반영합니다.")
    @PostMapping("/requests/{requestId}/approve")
    public ResponseEntity<Void> approveChangeRequest(@PathVariable Long requestId) {
        courseService.approveChangeRequest(requestId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강좌 변경 요청 반려", description = "대기 중인 변경 요청을 반려합니다.")
    @PostMapping("/requests/{requestId}/reject")
    public ResponseEntity<Void> rejectChangeRequest(@PathVariable Long requestId, @RequestParam String reason) {
        courseService.rejectChangeRequest(requestId, reason);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "담당 교사 변경", description = "강좌의 담당 교사를 변경합니다. (시간표 중복 확인 포함)")
    @PostMapping("/{courseId}/change-teacher")
    public ResponseEntity<Void> changeTeacher(@PathVariable Long courseId, @RequestParam Long newTeacherId) {
        courseService.changeTeacher(courseId, newTeacherId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "강좌 삭제 (DeleteCourse)", description = "운영 중인 강좌를 폐강(삭제) 처리합니다. (상태: CANCELED, 수강생 일괄 취소)")
    @PostMapping("/{courseId}/cancel")
    public ResponseEntity<Void> deleteCourse(@PathVariable Long courseId, @RequestParam String reason) {
        courseService.deleteCourse(courseId, reason);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "수강생 상세 조회", description = "수강생의 출결 및 과제 현황, 메모를 조회합니다.")
    @GetMapping("/{courseId}/students/{studentId}")
    public ResponseEntity<StudentDetailResDTO> getStudentDetail(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(courseService.getStudentDetail(courseId, studentId));
    }

    @Operation(summary = "수강생 강제 취소", description = "특정 수강생의 수강 신청을 강제로 취소합니다. (사유 필수)")
    @PostMapping("/{courseId}/students/{studentId}/force-cancel")
    public ResponseEntity<Void> forceCancelStudent(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @RequestParam String reason) {
        courseService.forceCancelStudent(courseId, studentId, reason);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "시간표 격자 조회", description = "지정된 학기/사용자의 시간표를 격자(Grid) 형태로 조회합니다. (미구현)")
    @GetMapping("/timetable")
    public void getTimetable(@RequestParam String semester, @RequestParam Long userId) {
        // ... (Existing placeholder)
    }
}
