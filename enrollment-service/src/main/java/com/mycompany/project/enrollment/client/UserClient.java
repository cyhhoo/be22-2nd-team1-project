package com.mycompany.project.enrollment.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "swcamp-user-service")
public interface UserClient {

    @GetMapping("/api/v1/internal/users/students/{id}/exists")
    boolean existsByStudentId(@PathVariable("id") Long id);

    @GetMapping("/api/v1/internal/users/students/{id}/grade")
    Integer getStudentGrade(@PathVariable("id") Long id);

    @GetMapping("/api/v1/internal/users/teachers/{id}/exists")
    boolean existsByTeacherId(@PathVariable("id") Long id);
}
