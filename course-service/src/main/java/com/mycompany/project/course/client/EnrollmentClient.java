package com.mycompany.project.course.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "swcamp-enrollment-service")
public interface EnrollmentClient {

        @GetMapping("/api/v1/internal/enrollments/course/{courseId}")
        List<InternalEnrollmentResponse> getEnrollmentsByCourse(@PathVariable("courseId") Long courseId);

        @GetMapping("/api/v1/internal/enrollments/course/{courseId}/student/{studentId}")
        InternalEnrollmentResponse getStudentEnrollment(@PathVariable("courseId") Long courseId,
                        @PathVariable("studentId") Long studentId);

        @PatchMapping("/api/v1/internal/enrollments/course/{courseId}/student/{studentId}/memo")
        void updateEnrollmentMemo(@PathVariable("courseId") Long courseId, @PathVariable("studentId") Long studentId,
                        @RequestBody String memo);
}
