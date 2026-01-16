package com.mycompany.project.user.command.application.service;

import com.mycompany.project.common.repository.BulkUploadLogRepository;
import com.mycompany.project.schedule.command.domain.repository.SubjectRepository;
import com.mycompany.project.user.command.application.dto.*;
import com.mycompany.project.common.enums.Role;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceTest {

    @InjectMocks
    private UserCommandService userCommandService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private StudentDetailRepository studentDetailRepository;
    @Mock
    private TeacherDetailRepository teacherDetailRepository;
    @Mock
    private SubjectRepository subjectRepository;
    @Mock
    private AdminDetailRepository adminDetailRepository;
    @Mock
    private BulkUploadLogRepository bulkUploadLogRepository;

    @Test
    @DisplayName("?숈깮 ?깅줉 ?깃났")
    void registerUser_Student_Success() {
        // given
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("student@test.com");
        request.setPassword("password");
        request.setName("?숈깮");
        request.setRole(Role.STUDENT);
        request.setBirthDate(LocalDate.of(2005, 1, 1));

        StudentDetailRequest studentReq = new StudentDetailRequest();
        studentReq.setGrade(1);
        studentReq.setClassNo("1");
        studentReq.setStudentNo(10);
        request.setStudentDetail(studentReq);

        given(userRepository.existsByEmail(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn("encodedPass");
        given(userRepository.save(any(User.class))).willAnswer(invocation -> {
            User user = invocation.getArgument(0);
            return user; // Mock save returning the same user
        });

        // when
        Long result = userCommandService.registerUser(request);

        // then
        verify(userRepository).save(any(User.class));
        verify(studentDetailRepository).save(any(StudentDetail.class));
    }

    @Test
    @DisplayName("援먯궗 ?깅줉 ?깃났")
    void registerUser_Teacher_Success() {
        // given
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("teacher@test.com");
        request.setPassword("password");
        request.setName("援먯궗");
        request.setRole(Role.TEACHER);

        TeacherDetailRequest teacherReq = new TeacherDetailRequest();
        teacherReq.setHomeroomGrade(2);
        teacherReq.setHomeroomClass(2);
        request.setTeacherDetail(teacherReq);

        given(userRepository.existsByEmail(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn("encodedPass");

        // when
        userCommandService.registerUser(request);

        // then
        verify(userRepository).save(any(User.class));
        verify(teacherDetailRepository).save(any());
    }

    @Test
    @DisplayName("愿由ъ옄 ?깅줉 ?깃났")
    void registerUser_Admin_Success() {
        // given
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("admin@test.com");
        request.setPassword("password");
        request.setName("愿由ъ옄");
        request.setRole(Role.ADMIN);

        AdminDetailRequest adminReq = new AdminDetailRequest();
        adminReq.setLevel("1");
        request.setAdminDetail(adminReq);

        given(userRepository.existsByEmail(any())).willReturn(false);
        given(passwordEncoder.encode(any())).willReturn("encodedPass");

        // when
        userCommandService.registerUser(request);

        // then
        verify(userRepository).save(any(User.class));
        verify(adminDetailRepository).save(any());
    }

    @Test
    @DisplayName("?깅줉 ?ㅽ뙣 - ?대찓??以묐났")
    void registerUser_DuplicateEmail() {
        // given
        UserRegisterRequest request = new UserRegisterRequest();
        request.setEmail("duplicate@test.com");

        given(userRepository.existsByEmail(request.getEmail())).willReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> userCommandService.registerUser(request));
    }
}
