/*
package com.mycompany.project.course.service;

import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CourseServiceForcedCancellationTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private CourseRepository courseRepository;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Test
    @DisplayName("수강생 강제 취소 성공 - 증빙 자료(사유) 저장 및 상태 변경")
    void forceCancelStudent_Success() {
        // Given
        Long courseId = 1L;
        Long studentId = 100L;
        String reason = "Conduct Violation";

        Enrollment enrollment = Enrollment.builder()
                .courseId(courseId)
                .userId(studentId)
                .build();

        given(enrollmentRepository.findByCourseId(courseId)).willReturn(List.of(enrollment));

        // When
        courseService.forceCancelStudent(courseId, studentId, reason);

        // Then
        assertThat(enrollment.getStatus()).isEqualTo(Enrollment.EnrollmentStatus.FORCED_CANCELED);
        assertThat(enrollment.getCancellationReason()).isEqualTo(reason);
    }

    @Test
    @DisplayName("수강생 강제 취소 실패 - 사유 누락")
    void forceCancelStudent_Failure_NoReason() {
        // Given
        Long courseId = 1L;
        Long studentId = 100L;

        // When & Then
        assertThatThrownBy(() -> courseService.forceCancelStudent(courseId, studentId, ""))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("취소 사유는 필수 입력 값입니다.");
    }
}
*/
