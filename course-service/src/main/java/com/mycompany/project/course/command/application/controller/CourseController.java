package com.mycompany.project.course.command.application.controller;

import com.mycompany.project.course.command.application.dto.CourseCreateReqDTO;
import com.mycompany.project.course.command.application.dto.CourseUpdateReqDTO;
import com.mycompany.project.course.command.application.service.CourseService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.mycompany.project.course.command.application.dto.InternalCourseResponse;
import com.mycompany.project.course.command.application.dto.StudentDetailResDTO;
import com.mycompany.project.common.response.ApiResponse;
import org.springframework.security.access.prepost.PreAuthorize;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Course Management", description = "Course creation, query and timetable API")
@RestController
@RequestMapping("/api/v1/courses")
@RequiredArgsConstructor
public class CourseController {

    private final CourseService courseService;

    @Operation(summary = "Create course", description = "Submit new course creation request (status: PENDING)")
    @PostMapping
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Void>> createCourse(
            @RequestBody @Valid CourseCreateReqDTO dto) {
        courseService.createCourse(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(null));
    }

    @Operation(summary = "Approve course", description = "Approve submitted course to confirm creation (PENDING -> OPEN)")
    @PostMapping("/{courseId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> approveCourse(@PathVariable Long courseId) {
        courseService.approveCourse(courseId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Reject course", description = "Reject submitted course (PENDING -> REFUSE)")
    @PostMapping("/{courseId}/refuse")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> refuseCourse(@PathVariable Long courseId, @RequestParam String reason) {
        courseService.refuseCourse(courseId, reason);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Request course update", description = "Request changes to running course (requires admin approval)")
    @PostMapping("/{courseId}/request-update")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<ApiResponse<Long>> requestCourseUpdate(
            @PathVariable Long courseId,
            @RequestBody @Valid CourseUpdateReqDTO dto,
            @RequestParam String reason) {
        Long requestId = courseService.requestCourseUpdate(courseId, dto, reason);
        return ResponseEntity.ok(ApiResponse.success(requestId));
    }

    @Operation(summary = "Approve course update request", description = "Approve pending update request and apply changes")
    @PostMapping("/requests/{requestId}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> approveChangeRequest(@PathVariable Long requestId) {
        courseService.approveChangeRequest(requestId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Reject course update request", description = "Reject pending update request")
    @PostMapping("/requests/{requestId}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> rejectChangeRequest(@PathVariable Long requestId,
            @RequestParam String reason) {
        courseService.rejectChangeRequest(requestId, reason);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Change course teacher", description = "Change assigned teacher for the course (with timetable conflict check)")
    @PostMapping("/{courseId}/change-teacher")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changeTeacher(@PathVariable Long courseId,
            @RequestParam Long newTeacherId) {
        courseService.changeTeacher(courseId, newTeacherId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Delete course", description = "Cancel running course (status: CANCELED, refund enrollments)")
    @PostMapping("/{courseId}/cancel")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(@PathVariable Long courseId, @RequestParam String reason) {
        courseService.deleteCourse(courseId, reason);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Get student detail", description = "Get student attendance, assignment status and memo")
    @GetMapping("/{courseId}/students/{studentId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ApiResponse<StudentDetailResDTO>> getStudentDetail(
            @PathVariable Long courseId,
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getStudentDetail(courseId, studentId)));
    }

    @Operation(summary = "Force cancel enrollment", description = "Force cancel specific student enrollment (reason required)")
    @PostMapping("/{courseId}/students/{studentId}/force-cancel")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> forceCancelStudent(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @RequestParam String reason) {
        courseService.forceCancelStudent(courseId, studentId, reason);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Update student memo", description = "Update memo specific to this student in the course")
    @PutMapping("/{courseId}/students/{studentId}/memo")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> updateStudentMemo(
            @PathVariable Long courseId,
            @PathVariable Long studentId,
            @RequestBody String memo) {
        courseService.updateStudentMemo(courseId, studentId, memo);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Get course list by teacher", description = "Query paginated course list for specific teacher")
    @GetMapping("/teacher/{teacherId}")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<com.mycompany.project.course.command.application.dto.CourseListResDTO>>> getCourseList(
            @PathVariable Long teacherId,
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getCourseList(teacherId, pageable)));
    }

    @Operation(summary = "Get all courses (Admin)", description = "Query all courses with pagination (Admin only)")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<org.springframework.data.domain.Page<com.mycompany.project.course.command.application.dto.CourseListResDTO>>> getAllCourses(
            @org.springframework.data.web.PageableDefault(size = 10) org.springframework.data.domain.Pageable pageable) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getAllCourses(pageable)));
    }

    @Operation(summary = "Change course status", description = "Manually change course status (e.g. early close, reopen)")
    @PutMapping("/{courseId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changeCourseStatus(
            @PathVariable Long courseId,
            @RequestParam com.mycompany.project.course.command.domain.aggregate.CourseStatus status) {
        courseService.changeCourseStatus(courseId, status);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @Operation(summary = "Get teacher weekly timetable", description = "Query teacher's weekly schedule (by day/period)")
    @GetMapping("/teacher/{teacherId}/timetable")
    @PreAuthorize("hasAnyRole('TEACHER','ADMIN')")
    public ResponseEntity<ApiResponse<com.mycompany.project.course.command.application.dto.TeacherTimetableResDTO>> getTeacherTimetable(
            @PathVariable Long teacherId,
            @RequestParam(defaultValue = "1") Long semester) {
        return ResponseEntity.ok(ApiResponse.success(courseService.getTeacherTimetable(teacherId, semester)));
    }

    @Hidden
    @GetMapping("/internal/{courseId}")
    public ResponseEntity<InternalCourseResponse> getInternalCourseInfo(@PathVariable Long courseId) {
        return ResponseEntity.ok(courseService.getInternalCourseInfo(courseId));
    }

    @Hidden
    @PostMapping("/internal/{courseId}/increase-enrollment")
    public ResponseEntity<Void> increaseEnrollment(@PathVariable Long courseId) {
        courseService.increaseEnrollment(courseId);
        return ResponseEntity.ok().build();
    }

    @Hidden
    @PostMapping("/internal/{courseId}/decrease-enrollment")
    public ResponseEntity<Void> decreaseEnrollment(@PathVariable Long courseId) {
        courseService.decreaseEnrollment(courseId);
        return ResponseEntity.ok().build();
    }

    @Hidden
    @GetMapping("/internal/courses/academic-year/{academicYearId}")
    public ResponseEntity<java.util.List<InternalCourseResponse>> getInternalCoursesByAcademicYear(
            @PathVariable Long academicYearId) {
        return ResponseEntity.ok(courseService.getInternalCoursesByAcademicYear(academicYearId));
    }
}
