package com.mycompany.project.attendance.client;

import com.mycompany.project.common.dto.InternalStudentResponse;
import com.mycompany.project.common.dto.InternalStudentSearchRequest;
import com.mycompany.project.common.dto.UserInternalResponse;
import com.mycompany.project.common.dto.InternalTeacherResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "swcamp-user-service")
public interface UserClient {

    @GetMapping("/api/v1/internal/users/{userId}")
    UserInternalResponse getUserInfo(@PathVariable("userId") Long userId);

    @PostMapping("/api/v1/internal/users/students/search")
    List<InternalStudentResponse> searchStudents(@RequestBody InternalStudentSearchRequest request);

    @GetMapping("/api/v1/internal/users/teachers/{userId}")
    InternalTeacherResponse getTeacherInfo(@PathVariable("userId") Long userId);

    @GetMapping("/api/v1/internal/users/students/count-matched")
    Long countMatchedStudents(@RequestParam("studentIds") List<Long> studentIds,
            @RequestParam("grade") Integer grade,
            @RequestParam("classNo") String classNo);
}
