/*
package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.CourseCreateReqDTO;
import com.mycompany.project.course.dto.TimeSlotDTO;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.entity.CourseType;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class CourseServiceCancellationTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private EnrollmentRepository enrollmentRepository;

    @Test
    @Transactional
    @DisplayName("[Success] 강좌 폐강 시 상태 변경 및 수강생 일괄 취소")
    void cancelCourseSuccess() {
        // Given
        // 1. 강좌 생성 (OPEN)
        Course course = createOpenCourse("Cancel Test Course");
        Long courseId = course.getId();

        // 2. 수강생 등록 (3명)
        courseService.enrollStudents(courseId, List.of(10L, 20L, 30L), true);

        // When
        String reason = "인원 미달";
        courseService.deleteCourse(courseId, reason);

        // Then
        // 1. 강좌 상태 확인 (CANCELED)
        Course canceledCourse = courseRepository.findById(courseId).orElseThrow();
        assertThat(canceledCourse.getStatus()).isEqualTo(CourseStatus.CANCELED);

        // 2. 수강생 상태 확인 (FORCED_CANCELED)
        List<Enrollment> enrollments = enrollmentRepository.findByCourseId(courseId);
        assertThat(enrollments).hasSize(3);
        assertThat(enrollments).allMatch(e -> e.getStatus() == Enrollment.EnrollmentStatus.FORCED_CANCELED);
    }

    private Course createOpenCourse(String name) {
        CourseCreateReqDTO dto = new CourseCreateReqDTO();
        dto.setName(name);
        dto.setCourseType(CourseType.ELECTIVE);
        dto.setMaxCapacity(10);
        dto.setTuition(10000);
        dto.setSubjectId(1L);
        dto.setAcademicYearId(1L);
        dto.setTeacherDetailId(100L);

        TimeSlotDTO slot = new TimeSlotDTO();
        slot.setDayOfWeek("FRI");
        slot.setPeriod(1);
        slot.setClassroom("Room 101");
        dto.setTimeSlots(Collections.singletonList(slot));

        courseService.createCourse(dto);

        // PENDING -> OPEN (for simplified setup)
        Course course = courseRepository.findAll().stream()
                .filter(c -> c.getName().equals(name))
                .findFirst()
                .orElseThrow();

        course.changeStatus(CourseStatus.OPEN);
        return courseRepository.save(course);
    }

    @Test
    @DisplayName("[Failure] 존재하지 않는 강좌 폐강 시 예외 발생")
    void cancelCourseFail_NotFound() {
        // Given
        Long invalidId = 9999L;
        String reason = "Admin Cancel";

        // When & Then
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, () -> {
            courseService.deleteCourse(invalidId, reason);
        });
    }
}
*/
