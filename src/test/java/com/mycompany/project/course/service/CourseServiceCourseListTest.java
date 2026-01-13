package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.CourseListResDTO;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.entity.CourseType;
import com.mycompany.project.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CourseServiceCourseListTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Test
    @DisplayName("교사별 강좌 목록 조회 - 페이징 확인")
    void getCourseList_Teacher_Success() {
        // Given
        Long teacherId = 10L;
        Pageable pageable = PageRequest.of(0, 10);

        Course course1 = Course.builder()
                .name("Calculus")
                .courseType(CourseType.MANDATORY)
                .status(CourseStatus.OPEN)
                .teacherDetailId(teacherId)
                .maxCapacity(30)
                .build();

        // Reflection for ID and currentCount
        org.springframework.test.util.ReflectionTestUtils.setField(course1, "id", 1L);
        org.springframework.test.util.ReflectionTestUtils.setField(course1, "currentCount", 5);

        Page<Course> coursePage = new PageImpl<>(List.of(course1));

        given(courseRepository.findByTeacherDetailId(teacherId, pageable)).willReturn(coursePage);

        // When
        Page<CourseListResDTO> result = courseService.getCourseList(teacherId, pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent().get(0).getName()).isEqualTo("Calculus");
        assertThat(result.getContent().get(0).getTeacherName()).isEqualTo("Teacher_" + teacherId);
        assertThat(result.getContent().get(0).getCurrentCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("전체 강좌 목록 조회 - 관리자용")
    void getAllCourses_Admin_Success() {
        // Given
        Pageable pageable = PageRequest.of(0, 10);

        Course course1 = Course.builder()
                .id(1L)
                .name("Math")
                .teacherDetailId(10L)
                .maxCapacity(30)
                .build();
        Course course2 = Course.builder()
                .id(2L)
                .name("Science")
                .teacherDetailId(11L)
                .maxCapacity(30)
                .build();

        Page<Course> coursePage = new PageImpl<>(List.of(course1, course2));

        given(courseRepository.findAll(pageable)).willReturn(coursePage);

        // When
        Page<CourseListResDTO> result = courseService.getAllCourses(pageable);

        // Then
        assertThat(result.getTotalElements()).isEqualTo(2);
        assertThat(result.getContent()).extracting("name").containsExactly("Math", "Science");
    }
}
