package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.CourseUpdateReqDTO;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.entity.CourseType;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.schedule.command.domain.aggregate.Subject;
import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.TeacherDetail;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.aggregate.UserStatus;
import com.mycompany.project.user.command.domain.repository.SubjectRepository;
import com.mycompany.project.user.command.domain.repository.TeacherDetailRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseServiceTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeacherDetailRepository teacherDetailRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    private TeacherDetail createAndSaveTeacher() {
        // 1. User 생성 및 저장
        User user = User.builder()
                .email("teacher_test_" + System.currentTimeMillis() + "@test.com")
                .password("password123")
                .name("Teacher Test")
                .role(Role.TEACHER)
                .status(UserStatus.ACTIVE)
                .build();
        userRepository.save(user);

        // 2. Subject 생성 및 저장
        Subject subject = Subject.builder()
                .name("Mathematics")
                .build();
        subjectRepository.save(subject);

        // 3. TeacherDetail 생성 및 저장
        TeacherDetail teacherDetail = TeacherDetail.builder()
                .user(user)
                .subject(subject)
                .homeroomGrade(1)
                .homeroomClassNo(1)
                .build();
        return teacherDetailRepository.save(teacherDetail);
    }

    @Test
    @DisplayName("강좌 엔티티 기본 저장 테스트")
    @Transactional
    void insertTest() {
        TeacherDetail teacher = createAndSaveTeacher();

        Course newCourse = Course.builder()
                .name("Test Course")
                .academicYearId(1L)
                .maxCapacity(10)
                .teacherDetail(teacher)
                .courseType(CourseType.MANDATORY)
                .build();

        Course course = courseRepository.save(newCourse);
        assertNotNull(course);
        assertNotNull(course.getCourseId());
    }

    @Test
    @Transactional
    @DisplayName("강좌 정보 수정 성공 테스트")
    void updateTest() {
        // 1. 데이터 준비
        TeacherDetail teacher = createAndSaveTeacher();

        Course newCourse = Course.builder()
                .name("Original Name")
                .academicYearId(1L)
                .maxCapacity(10)
                .teacherDetail(teacher)
                .courseType(CourseType.MANDATORY)
                .tuition(100000)
                .build();
        Course savedCourse = courseRepository.save(newCourse);

        // 2. 수정 데이터 준비 (DTO)
        CourseUpdateReqDTO updateReqDTO = new CourseUpdateReqDTO();
        updateReqDTO.setName("Updated Name");
        updateReqDTO.setCourseType(CourseType.ELECTIVE);
        updateReqDTO.setMaxCapacity(20);
        updateReqDTO.setTuition(200000);
        updateReqDTO.setStatus(CourseStatus.CLOSED);

        // 3. 수정 서비스 호출
        courseService.updateCourse(savedCourse.getCourseId(), updateReqDTO);

        // 4. 조회 및 검증
        Course updatedCourse = courseRepository.findById(savedCourse.getCourseId()).orElseThrow();

        assertEquals("Updated Name", updatedCourse.getName());
        assertEquals(CourseType.ELECTIVE, updatedCourse.getCourseType());
        assertEquals(20, updatedCourse.getMaxCapacity());
        assertEquals(200000, updatedCourse.getTuition());
        assertEquals(CourseStatus.CLOSED, updatedCourse.getStatus());
    }

    @Test
    @DisplayName("강좌 정보 수정 실패 - 존재하지 않는 강좌 ID")
    void updateTest_Fail_NotFound() {
        // 1. 존재하지 않는 ID와 DTO 준비
        Long invalidId = 9999L;
        CourseUpdateReqDTO dto = new CourseUpdateReqDTO();
        dto.setName("New Name");

        // 2. 서비스 호출 및 예외 검증
        assertThatThrownBy(() -> courseService.updateCourse(invalidId, dto))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(ErrorCode.COURSE_NOT_FOUND);
    }
}
