package com.mycompany.project.user.command.application.service;

import com.mycompany.project.schedule.command.domain.aggregate.Subject;
import com.mycompany.project.user.command.domain.aggregate.AdminDetail;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import com.mycompany.project.user.command.domain.aggregate.TeacherDetail;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
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
  private AdminDetailRepository adminDetailRepository;
  @Mock
  private SubjectRepository subjectRepository;

  @Test
  @DisplayName("CSV 파일로 학생, 교사, 관리자를 일괄 등록한다")
  void importUser_success() {
    // given
    String csvContent = "email,name,password,roleStr,birthDate,grade,classNo,studentNo,subject,homeroomGrade,homeroomClass,adminLevel\n" +
        "student@test.com,학생1,1234,ROLE_STUDENT,2010-01-01,1,2,15,,,,\n" +
        "teacher@test.com,교사1,1234,ROLE_TEACHER,1980-01-01,,,,Math,3,5,\n" +
        "admin@test.com,관리자1,1234,ROLE_ADMIN,1975-01-01,,,,,,,1";
    MockMultipartFile file = new MockMultipartFile(
        "file", "users.csv", "text/csv", csvContent.getBytes(StandardCharsets.UTF_8)
    );
    // Mocking: 교사의 과목 조회 시 Subject 반환
    given(subjectRepository.findByName("Math"))
        .willReturn(Optional.of(Subject.builder().id(1L).name("Math").build()));

    // Mocking: 비밀번호 인코딩
    given(passwordEncoder.encode(any())).willReturn("encodedPassword");

    // when
    int count = userCommandService.importUser(file);

    // then
    assertEquals(3, count); // 총 3명 등록 확인

    // 데이터 검증 (ArgumentCaptor 사용)
    // 1. StudentDetail 저장 검증
    ArgumentCaptor<StudentDetail> studentCaptor = ArgumentCaptor.forClass(StudentDetail.class);
    verify(studentDetailRepository).save(studentCaptor.capture());
    StudentDetail savedStudent = studentCaptor.getValue();

    assertEquals("학생1", savedStudent.getUser().getName()); // User 객체 연결 확인
    assertEquals(1, savedStudent.getGrade());
    assertEquals("2", savedStudent.getClassNo());
    assertEquals(15, savedStudent.getStudentNo());

    // 2. TeacherDetail 저장 검증
    ArgumentCaptor<TeacherDetail> teacherCaptor = ArgumentCaptor.forClass(TeacherDetail.class);
    verify(teacherDetailRepository).save(teacherCaptor.capture());
    TeacherDetail savedTeacher = teacherCaptor.getValue();

    assertEquals("Math", savedTeacher.getSubject().getName());
    assertEquals(3, savedTeacher.getHomeroomGrade());
    assertEquals(5, savedTeacher.getHomeroomClassNo());

    // 3. AdminDetail 저장 검증
    ArgumentCaptor<AdminDetail> adminCaptor = ArgumentCaptor.forClass(AdminDetail.class);
    verify(adminDetailRepository).save(adminCaptor.capture());
    AdminDetail savedAdmin = adminCaptor.getValue();

    assertEquals("관리자1", savedAdmin.getUser().getName());
    assertEquals(AdminDetail.AdminLevel.LEVEL_1, savedAdmin.getLevel()); // 1 -> LEVEL_1 매핑 확인
  }
}