package com.mycompany.project.user.command.application.controller;

import com.mycompany.project.common.dto.*;
import com.mycompany.project.user.command.application.service.UserCommandService;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.StudentDetailRepository;
import com.mycompany.project.user.command.domain.repository.TeacherDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final TeacherDetailRepository teacherDetailRepository;
    private final StudentDetailRepository studentDetailRepository;
    private final UserCommandService userCommandService;

    @GetMapping("/{userId}")
    public UserInternalResponse getUserInfo(@PathVariable("userId") Long userId) {
        User user = userCommandService.findById(userId);
        if (user == null) {
            return null;
        }
        return convertToInternalResponse(user);
    }

    @GetMapping("/teachers/{id}/exists")
    public boolean existsByTeacherId(@PathVariable("id") Long id) {
        java.util.Objects.requireNonNull(id, "Teacher ID must not be null");
        return teacherDetailRepository.existsById(id);
    }

    @GetMapping("/students/{id}/exists")
    public boolean existsByStudentId(@PathVariable("id") Long id) {
        java.util.Objects.requireNonNull(id, "Student ID must not be null");
        return studentDetailRepository.existsById(id);
    }

    @GetMapping("/students/{id}/grade")
    public Integer getStudentGrade(@PathVariable("id") Long id) {
        return studentDetailRepository.findById(id)
                .map(StudentDetail::getGrade)
                .orElse(null);
    }

    @GetMapping("/auth")
    public UserAuthResponse getUserForAuth(@RequestParam("email") String email) {
        User user = userCommandService.findByEmail(email);
        if (user == null) {
            return null;
        }
        UserAuthResponse res = new UserAuthResponse();
        res.setUserId(user.getUserId());
        res.setEmail(user.getEmail());
        res.setPassword(user.getPassword());
        res.setRole(user.getRole());
        res.setStatus(user.getStatus());
        res.setLocked(user.isLocked());
        return res;
    }

    @PostMapping("/login-success")
    public void recordLoginSuccess(@RequestParam("email") String email) {
        userCommandService.recordLoginSuccess(email);
    }

    @PostMapping("/login-fail")
    public void recordLoginFail(@RequestParam("email") String email) {
        userCommandService.recordLoginFail(email);
    }

    @GetMapping("/email")
    public UserInternalResponse getByEmail(@RequestParam("email") String email) {
        User user = userCommandService.findByEmail(email);
        if (user == null) {
            return null;
        }
        return convertToInternalResponse(user);
    }

    @PostMapping("/activate")
    public void activateUser(@RequestBody UserInternalActivateRequest request) {
        userCommandService.internalActivate(request.getEmail(), request.getEncryptedPassword());
    }

    @PostMapping("/students/search")
    public List<InternalStudentResponse> searchStudents(@RequestBody InternalStudentSearchRequest request) {
        return studentDetailRepository.findByIdInAndGradeAndClassNo(
                request.getStudentIds(), request.getGrade(), request.getClassNo()).stream()
                .map(s -> new InternalStudentResponse(s.getId(), s.getGrade(), s.getClassNo()))
                .collect(Collectors.toList());
    }

    @GetMapping("/teachers/{userId}")
    public InternalTeacherResponse getTeacherInfo(@PathVariable("userId") Long userId) {
        return teacherDetailRepository.findById(userId)
                .map(t -> InternalTeacherResponse.builder()
                        .id(t.getId())
                        .name(t.getUser().getName())
                        .email(t.getUser().getEmail())
                        .homeroomGrade(t.getHomeroomGrade())
                        .homeroomClassNo(t.getHomeroomClassNo())
                        .build())
                .orElse(null);
    }

    @GetMapping("/students/count-matched")
    public Long countMatchedStudents(@RequestParam("studentIds") List<Long> studentIds,
            @RequestParam("grade") Integer grade,
            @RequestParam("classNo") String classNo) {
        return studentDetailRepository.countByIdInAndGradeAndClassNo(studentIds, grade, classNo);
    }

    private UserInternalResponse convertToInternalResponse(User user) {
        return UserInternalResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .role(user.getRole())
                .status(user.getStatus())
                .birthDate(user.getBirthDate())
                .authCode(user.getAuthCode())
                .build();
    }
}
