package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.CourseCreateReqDTO;
import com.mycompany.project.course.dto.TimeSlotDTO;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseType;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CourseServiceEnrollmentTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    @Transactional
    @DisplayName("[Success] 정원 내 일괄 등록 성공 (개별 등록 포함)")
    void enrollStudentsSuccess() {
        // Given
        Course course = createCourse("Normal Course", CourseType.MANDATORY, 10, "MON", 1);
        List<Long> students = Arrays.asList(10L, 11L, 12L);

        // When
        courseService.enrollStudents(course.getId(), students, false);

        // Then
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());
        assertThat(enrollments).hasSize(3);
        assertThat(enrollments).extracting("userId").containsExactlyInAnyOrder(10L, 11L, 12L);
    }

    @Test
    @Transactional
    @DisplayName("[Success] 1명 개별 등록 성공")
    void enrollSingleStudentSuccess() {
        // Given
        Course course = createCourse("Single Course", CourseType.MANDATORY, 10, "MON", 1);
        List<Long> students = Collections.singletonList(99L); // 1명만 리스트에 담아서 전달

        // When
        courseService.enrollStudents(course.getId(), students, false);

        // Then
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());
        assertThat(enrollments).hasSize(1);
        assertThat(enrollments.get(0).getUserId()).isEqualTo(99L);
    }

    @Test
    @Transactional
    @DisplayName("[Failure] 정원 초과 시 예외 발생 (Force=false)")
    void enrollStudentsFail_Capacity() {
        // Given
        Course course = createCourse("Small Course", CourseType.MANDATORY, 2, "MON", 1);
        List<Long> students = Arrays.asList(10L, 11L, 12L); // 3명 시도

        // When & Then
        assertThatThrownBy(() -> courseService.enrollStudents(course.getId(), students, false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("수강 정원이 초과되었습니다");
    }

    @Test
    @Transactional
    @DisplayName("[Success] 강제 등록으로 정원 초과 무시 (Force=true)")
    void enrollStudentsSuccess_Force() {
        // Given
        Course course = createCourse("Small Course Force", CourseType.MANDATORY, 2, "MON", 1);
        List<Long> students = Arrays.asList(10L, 11L, 12L);

        // When
        courseService.enrollStudents(course.getId(), students, true);

        // Then
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(course.getId());
        assertThat(enrollments).hasSize(3);
    }

    @Test
    @Transactional
    @DisplayName("[Success] 시간표 중복 시 선택과목(ELECTIVE) 자동 취소 및 신규 등록")
    void enrollStudentsAutoCancelElective() {
        // Given
        Long studentId = 50L;
        // 1. 기존 선택 과목 (MON 1교시)
        Course oldElective = createCourse("Old Elective", CourseType.ELECTIVE, 10, "MON", 1);
        enrollmentRepository.save(Enrollment.builder().userId(studentId).courseId(oldElective.getId()).build());

        // 2. 신규 과목 (MON 1교시)
        Course newCourse = createCourse("New Course", CourseType.MANDATORY, 10, "MON", 1);

        // When
        courseService.enrollStudents(newCourse.getId(), Collections.singletonList(studentId), false);

        // Then
        Enrollment oldEnrollment = enrollmentRepository.findByCourseId(oldElective.getId()).get(0);
        assertThat(oldEnrollment.getStatus()).isEqualTo(Enrollment.EnrollmentStatus.CANCELED);

        List<Enrollment> newEnrollments = enrollmentRepository.findByCourseId(newCourse.getId());
        assertThat(newEnrollments).hasSize(1);
        assertThat(newEnrollments.get(0).getStatus()).isEqualTo(Enrollment.EnrollmentStatus.APPLIED);
    }

    @Test
    @Transactional
    @DisplayName("[Failure] 시간표 중복 시 필수과목(MANDATORY)이면 예외 발생")
    void enrollStudentsFail_MandatoryConflict() {
        // Given
        Long studentId = 60L;
        // 1. 기존 필수 과목 (MON 1교시)
        Course oldMandatory = createCourse("Old Mandatory", CourseType.MANDATORY, 10, "MON", 1);
        enrollmentRepository.save(Enrollment.builder().userId(studentId).courseId(oldMandatory.getId()).build());

        // 2. 신규 과목 (MON 1교시)
        Course newCourse = createCourse("New Course", CourseType.ELECTIVE, 10, "MON", 1);

        // When & Then
        assertThatThrownBy(
                () -> courseService.enrollStudents(newCourse.getId(), Collections.singletonList(studentId), false))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("필수 과목");
    }

    // Helper
    private Course createCourse(String name, CourseType type, int capacity, String day, int period) {
        CourseCreateReqDTO dto = new CourseCreateReqDTO();
        dto.setName(name);
        dto.setCourseType(type);
        dto.setMaxCapacity(capacity);
        dto.setTuition(10000);
        dto.setSubjectId(1L);
        dto.setAcademicYearId(1L);
        dto.setTeacherDetailId(100L);

        TimeSlotDTO slot = new TimeSlotDTO();
        slot.setDayOfWeek(day);
        slot.setPeriod(period);
        slot.setClassroom("Room 101");
        dto.setTimeSlots(Collections.singletonList(slot));

        courseService.createCourse(dto);

        return courseRepository.findAll().stream()
                .filter(c -> c.getName().equals(name))
                .reduce((first, second) -> second)
                .orElseThrow();
    }
}
