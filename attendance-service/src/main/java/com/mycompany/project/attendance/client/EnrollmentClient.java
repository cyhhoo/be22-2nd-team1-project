package com.mycompany.project.attendance.client;

import com.mycompany.project.attendance.client.dto.EnrollmentSearchRequest;
import com.mycompany.project.attendance.client.dto.InternalEnrollmentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "swcamp-enrollment-service", url = "${gateway.url}")
public interface EnrollmentClient {

    @PostMapping("/api/v1/enrollments/internal/enrollments/search")
    List<InternalEnrollmentResponse> searchEnrollments(@RequestBody EnrollmentSearchRequest request);

    @GetMapping("/api/v1/enrollments/internal/course/{courseId}")
    List<InternalEnrollmentResponse> getInternalEnrollments(@PathVariable("courseId") Long courseId,
            @org.springframework.web.bind.annotation.RequestParam("status") String status);

    @GetMapping("/api/v1/enrollments/internal/{enrollmentId}")
    InternalEnrollmentResponse getInternalEnrollment(@PathVariable("enrollmentId") Long enrollmentId);
}
