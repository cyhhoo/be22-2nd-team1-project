package com.mycompany.project.course.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service")
public interface UserClient {

    @GetMapping("/api/v1/internal/users/teachers/{id}/exists")
    boolean existsByTeacherId(@PathVariable("id") Long id);

    @GetMapping("/api/v1/internal/users/students/{id}/exists")
    boolean existsByStudentId(@PathVariable("id") Long id);
}
