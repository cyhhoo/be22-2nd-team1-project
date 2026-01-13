package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.CourseCreateReqDTO;
import com.mycompany.project.course.dto.TimeSlotDTO;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseType;
import com.mycompany.project.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CourseServiceTeacherChangeTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Test
    @Transactional
    @DisplayName("담당 교사 변경 성공")
    void changeTeacherSuccess() {
        // Given
        Long oldTeacherId = 100L;
        Long newTeacherId = 200L;
        Course course = createCourseWithTeacher(oldTeacherId, "MON", 1);
        Long courseId = course.getId();

        // When
        courseService.changeTeacher(courseId, newTeacherId);

        // Then
        Course updatedCourse = courseRepository.findById(courseId).orElseThrow();
        assertThat(updatedCourse.getTeacherDetailId()).isEqualTo(newTeacherId);
    }

    @Test
    @Transactional
    @DisplayName("교사 스케줄 중복으로 변경 실패")
    void changeTeacherFail_TimeConflict() {
        // Given
        // 1. 기존 강좌 (Teacher A, MON 1)
        createCourseWithTeacher(200L, "MON", 1);

        // 2. 변경 대상 강좌 (Teacher B -> Teacher A로 변경 시도)
        Course targetCourse = createCourseWithTeacher(300L, "MON", 1);

        // When & Then
        assertThatThrownBy(() -> courseService.changeTeacher(targetCourse.getId(), 200L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("이미 수업이 있습니다");
    }

    private Course createCourseWithTeacher(Long teacherId, String day, Integer period) {
        CourseCreateReqDTO dto = new CourseCreateReqDTO();
        dto.setName("Test Course");
        dto.setCourseType(CourseType.MANDATORY);
        dto.setMaxCapacity(30);
        dto.setTuition(10000);
        dto.setSubjectId(1L);
        dto.setAcademicYearId(1L);
        dto.setTeacherDetailId(teacherId);

        TimeSlotDTO slot = new TimeSlotDTO();
        slot.setDayOfWeek(day);
        slot.setPeriod(period);
        slot.setClassroom("Room 101");
        dto.setTimeSlots(Collections.singletonList(slot));

        // Use service to create (populates DB tables for conflict check)
        courseService.createCourse(dto);

        // Find the created course (assuming it's the latest or strictly controlled env)
        // For simplicity in this specific test helper:
        return courseRepository.findAll().stream()
                .filter(c -> c.getTeacherDetailId().equals(teacherId))
                .reduce((first, second) -> second) // get last
                .orElseThrow();
    }
}