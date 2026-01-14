package com.mycompany.project.enrollment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "swcamp-course-service", url = "${gateway.url}")
public interface CourseClient {

    @GetMapping("/courses/internal/{courseId}")
    InternalCourseResponse getInternalCourseInfo(@PathVariable("courseId") Long courseId);

    @PostMapping("/courses/internal/{courseId}/increase-enrollment")
    void increaseEnrollment(@PathVariable("courseId") Long courseId);

    @PostMapping("/courses/internal/{courseId}/decrease-enrollment")
    void decreaseEnrollment(@PathVariable("courseId") Long courseId);
}
