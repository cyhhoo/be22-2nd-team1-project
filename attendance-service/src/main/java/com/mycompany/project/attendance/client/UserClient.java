package com.mycompany.project.attendance.client;

import com.mycompany.project.attendance.client.dto.InternalStudentResponse;
import com.mycompany.project.attendance.client.dto.StudentSearchRequest;
import com.mycompany.project.attendance.client.dto.UserResponse;
import com.mycompany.project.attendance.client.dto.InternalTeacherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@FeignClient(name = "swcamp-user-service", url = "${gateway.url}")
public interface UserClient {

    @GetMapping("/user/internal/{userId}")
    UserResponse getUserInfo(@PathVariable("userId") Long userId);

    @PostMapping("/user/internal/students/search")
    List<InternalStudentResponse> searchStudents(@RequestBody StudentSearchRequest request);

    @GetMapping("/user/internal/teacher/{userId}")
    InternalTeacherResponse getTeacherInfo(@PathVariable("userId") Long userId);

    @GetMapping("/user/internal/students/count-matched")
    Long countMatchedStudents(@org.springframework.web.bind.annotation.RequestParam("studentIds") List<Long> studentIds,
            @org.springframework.web.bind.annotation.RequestParam("grade") Integer grade,
            @org.springframework.web.bind.annotation.RequestParam("classNo") String classNo);
}
