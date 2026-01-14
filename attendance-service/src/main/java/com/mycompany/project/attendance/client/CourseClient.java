package com.mycompany.project.attendance.client;

import com.mycompany.project.attendance.client.dto.InternalCourseResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "swcamp-course-service", url = "${gateway.url}")
public interface CourseClient {

    @GetMapping("/api/v1/courses/internal/courses/academic-year/{academicYearId}")
    List<InternalCourseResponse> getInternalCoursesByAcademicYear(@PathVariable("academicYearId") Long academicYearId);

    @GetMapping("/api/v1/courses/internal/{courseId}")
    InternalCourseResponse getInternalCourseInfo(@PathVariable("courseId") Long courseId);
}
