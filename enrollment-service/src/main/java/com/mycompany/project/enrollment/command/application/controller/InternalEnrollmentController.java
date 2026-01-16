package com.mycompany.project.enrollment.command.application.controller;

import com.mycompany.project.enrollment.command.application.dto.InternalEnrollmentResponse;
import com.mycompany.project.enrollment.command.domain.aggregate.Enrollment;
import com.mycompany.project.enrollment.command.domain.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/internal/enrollments")
@RequiredArgsConstructor
public class InternalEnrollmentController {

        private final EnrollmentRepository enrollmentRepository;

        @GetMapping("/course/{courseId}")
        public List<InternalEnrollmentResponse> getEnrollmentsByCourse(@PathVariable("courseId") Long courseId) {
                return enrollmentRepository.findByCourseId(courseId).stream()
                                .map(e -> new InternalEnrollmentResponse(e.getEnrollmentId(), e.getStudentDetailId(),
                                                e.getCourseId(),
                                                e.getStatus().name(), e.getMemo()))
                                .collect(Collectors.toList());
        }

        @GetMapping("/course/{courseId}/student/{studentId}")
        public InternalEnrollmentResponse getStudentEnrollment(@PathVariable("courseId") Long courseId,
                        @PathVariable("studentId") Long studentId) {
                return enrollmentRepository.findByCourseIdAndStudentDetailId(courseId, studentId)
                                .map(e -> new InternalEnrollmentResponse(e.getEnrollmentId(), e.getStudentDetailId(),
                                                e.getCourseId(),
                                                e.getStatus().name(), e.getMemo()))
                                .orElse(null);
        }

        @PatchMapping("/course/{courseId}/student/{studentId}/memo")
        public void updateEnrollmentMemo(@PathVariable("courseId") Long courseId,
                        @PathVariable("studentId") Long studentId,
                        @RequestBody String memo) {
                Enrollment enrollment = enrollmentRepository.findByCourseIdAndStudentDetailId(courseId, studentId)
                                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
                enrollment.updateMemo(memo);
                enrollmentRepository.save(enrollment);
        }
}
