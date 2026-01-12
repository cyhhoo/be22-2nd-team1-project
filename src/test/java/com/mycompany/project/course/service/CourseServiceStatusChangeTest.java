/*
package com.mycompany.project.course.service;

import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CourseServiceStatusChangeTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Test
    @DisplayName("강좌 상태 변경 성공 - OPEN -> CLOSED (수동 마감)")
    void changeCourseStatus_ToClosed_Success() {
        // Given
        Long courseId = 1L;
        Course course = Course.builder().status(CourseStatus.OPEN).build();
        given(courseRepository.findById(courseId)).willReturn(Optional.of(course));

        // When
        courseService.changeCourseStatus(courseId, CourseStatus.CLOSED);

        // Then
        assertThat(course.getStatus()).isEqualTo(CourseStatus.CLOSED);
    }

    @Test
    @DisplayName("강좌 상태 변경 성공 - CLOSED -> OPEN (재오픈)")
    void changeCourseStatus_ToOpen_Success() {
        // Given
        Long courseId = 1L;
        Course course = Course.builder().status(CourseStatus.CLOSED).build();
        given(courseRepository.findById(courseId)).willReturn(Optional.of(course));

        // When
        courseService.changeCourseStatus(courseId, CourseStatus.OPEN);

        // Then
        assertThat(course.getStatus()).isEqualTo(CourseStatus.OPEN);
    }

    @Test
    @DisplayName("강좌 상태 변경 실패 - 허용되지 않은 상태 (PENDING)")
    void changeCourseStatus_InvalidStatus_Fail() {
        // Given
        Long courseId = 1L;
        Course course = Course.builder().status(CourseStatus.OPEN).build();
        given(courseRepository.findById(courseId)).willReturn(Optional.of(course));

        // When & Then
        assertThatThrownBy(() -> courseService.changeCourseStatus(courseId, CourseStatus.PENDING))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수동 상태 변경은 개설(OPEN) 또는 마감(CLOSED) 상태로만 가능합니다.");
    }
}
*/
