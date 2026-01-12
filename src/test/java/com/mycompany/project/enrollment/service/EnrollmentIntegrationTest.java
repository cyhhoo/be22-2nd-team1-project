package com.mycompany.project.enrollment.service;

import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseType;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.command.dto.BulkEnrollmentResult;
import com.mycompany.project.enrollment.command.service.EnrollmentCommandService;
import com.mycompany.project.enrollment.entity.Cart;
import com.mycompany.project.enrollment.repository.CartRepository;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.user.command.domain.aggregate.*;
import com.mycompany.project.user.command.domain.repository.StudentDetailRepository;
import com.mycompany.project.user.command.domain.repository.TeacherDetailRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class EnrollmentIntegrationTest {

  @Autowired EnrollmentCommandService enrollmentCommandService;
  @Autowired CourseRepository courseRepository;
  @Autowired StudentDetailRepository studentDetailRepository;
  @Autowired CartRepository cartRepository;
  @Autowired EnrollmentRepository enrollmentRepository;
  @Autowired UserRepository userRepository;

  // [추가 1] 선생님 저장용 리포지토리 주입
  @Autowired TeacherDetailRepository teacherDetailRepository;

  private Long userId;
  private Long courseId_Java;
  private Long courseId_Full;

  @BeforeEach
  void setUp() {
    // 1. 학생 생성
    User studentUser = User.builder()
        .name("test_student")
        .email("student@test.com")
        .password("1234")
        .birthDate("2000-01-01")
        .role(Role.STUDENT)
        .status(UserStatus.ACTIVE)
        .build();
    userRepository.save(studentUser);

    StudentDetail student = StudentDetail.builder().user(studentUser).grade(1).build();
    studentDetailRepository.save(student);
    this.userId = studentUser.getUserId();

    // 2. [추가 2] 선생님 생성 (강좌 생성을 위해 필수!)
    User teacherUser = User.builder()
        .name("Test Teacher")
        .email("teacher@test.com")
        .password("1234")
        .birthDate("1980-01-01")
        .role(Role.TEACHER)
        .status(UserStatus.ACTIVE)
        .build();
    userRepository.save(teacherUser);

    // TeacherDetail 객체 생성 및 저장
    // (TeacherDetail 생성자가 User를 받도록 되어 있다고 가정합니다.
    //  만약 빌더 패턴이면 .user(teacherUser) 또는 .teacherId(teacherUser.getUserId()) 형태로 맞게 수정해주세요.)
    // TeacherDetail teacher = new TeacherDetail(teacherUser);
    TeacherDetail teacher = TeacherDetail.builder().user(teacherUser).build();
    teacherDetailRepository.save(teacher);

    // 3. 강좌 생성 (정상 과목)
    Course javaCourse = Course.builder()
        .name("자바 기초")
        .maxCapacity(10)
        .currentCount(0)
        .tuition(100000)
        .courseType(CourseType.MANDATORY) // 필수값
        .teacherDetail(teacher)           // [핵심] 선생님 정보 주입 (필수값)
        .build();
    courseRepository.save(javaCourse);
    this.courseId_Java = javaCourse.getCourseId();

    // 4. 강좌 생성 (정원 초과 과목)
    Course fullCourse = Course.builder()
        .name("인기 폭발 강좌")
        .maxCapacity(5)
        .currentCount(5)
        .tuition(200000)
        .courseType(CourseType.ELECTIVE)  // 필수값
        .teacherDetail(teacher)           // [핵심] 선생님 정보 주입 (필수값)
        .build();
    courseRepository.save(fullCourse);
    this.courseId_Full = fullCourse.getCourseId();
  }

  @Test
  @DisplayName("시나리오 1: [성공] 장바구니 1개 신청 -> 성공 시 수강내역 생성 & 장바구니 삭제")
  void bulkRegister_Success() {
    // Given
    StudentDetail student = studentDetailRepository.findByUserId(userId).get();
    Course javaCourse = courseRepository.findById(courseId_Java).get();
    cartRepository.save(new Cart(student, javaCourse));

    // When
    List<BulkEnrollmentResult> results = enrollmentCommandService.bulkRegister(userId, List.of(courseId_Java));

    // Then
    assertThat(results).hasSize(1);
    assertThat(results.get(0).isSuccess()).isTrue();
    assertThat(enrollmentRepository.existsByStudentDetailAndCourse(student, javaCourse)).isTrue();
    assertThat(cartRepository.existsByStudentDetailAndCourse(student, javaCourse)).isFalse();
  }

  @Test
  @DisplayName("시나리오 2: [혼합] 정상 과목 + 정원 초과 과목 동시 신청 -> 부분 성공 & 부분 삭제")
  void bulkRegister_PartialSuccess() {
    // Given
    StudentDetail student = studentDetailRepository.findByUserId(userId).get();
    Course javaCourse = courseRepository.findById(courseId_Java).get();
    Course fullCourse = courseRepository.findById(courseId_Full).get();

    cartRepository.save(new Cart(student, javaCourse));
    cartRepository.save(new Cart(student, fullCourse));

    // When
    List<BulkEnrollmentResult> results = enrollmentCommandService.bulkRegister(userId, List.of(courseId_Java, courseId_Full));

    // Then
    BulkEnrollmentResult resultJava = results.stream().filter(r -> r.getCourseId().equals(courseId_Java)).findFirst().get();
    BulkEnrollmentResult resultFull = results.stream().filter(r -> r.getCourseId().equals(courseId_Full)).findFirst().get();

    assertThat(resultJava.isSuccess()).isTrue();
    assertThat(resultFull.isSuccess()).isFalse();

    assertThat(cartRepository.existsByStudentDetailAndCourse(student, javaCourse)).isFalse();
    assertThat(cartRepository.existsByStudentDetailAndCourse(student, fullCourse)).isTrue();
  }
}