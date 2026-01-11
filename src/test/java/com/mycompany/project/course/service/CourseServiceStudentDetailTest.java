package com.mycompany.project.course.service;

import com.mycompany.project.attendance.repository.AttendanceRepository;
import com.mycompany.project.course.dto.StudentDetailResDTO;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class CourseServiceStudentDetailTest {

    @InjectMocks
    private CourseService courseService;

    @Mock
    private EnrollmentRepository enrollmentRepository;

    @Mock
    private AttendanceRepository attendanceRepository;

    @Mock
    private CourseRepository courseRepository;

    @Test
    @DisplayName("수강생 상세 조회 성공")
    void getStudentDetail_Success() {
        // Given
        Long courseId = 1L;
        Long studentId = 10L;
        Long enrollmentId = 100L;
        String memo = "Good Student";

        Enrollment enrollment = Enrollment.builder().userId(studentId).courseId(courseId).build();
        // Use Reflection to set ID and Memo
        org.springframework.test.util.ReflectionTestUtils.setField(enrollment, "enrollmentId", enrollmentId);
        org.springframework.test.util.ReflectionTestUtils.setField(enrollment, "memo", memo);

        given(enrollmentRepository.findByCourseId(courseId)).willReturn(List.of(enrollment));
        given(attendanceRepository.countByEnrollmentIdAndStatus(eq(enrollmentId), any())).willReturn(3L); // Simplified
                                                                                                          // count

        // When
        StudentDetailResDTO result = courseService.getStudentDetail(courseId, studentId);

        // Then
        assertThat(result.getStudentId()).isEqualTo(studentId);
        assertThat(result.getMemo()).isEqualTo(memo);
        assertThat(result.getAttendancePresent()).isEqualTo(3L);
    }

    @Test
    @DisplayName("수강생 상세 조회 실패 - 수강 이력 없음")
    void getStudentDetail_Fail_NoEnrollment() {
        // Given
        Long courseId = 1L;
        Long studentId = 10L;

        given(enrollmentRepository.findByCourseId(courseId)).willReturn(List.of()); // No enrollments

        // When & Then
        assertThatThrownBy(() -> courseService.getStudentDetail(courseId, studentId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수강생 정보를 찾을 수 없습니다");
    }

    @Test
    @DisplayName("수강생 메모 수정 성공")
    void updateStudentMemo_Success() {
        // Given
        Long courseId = 1L;
        Long studentId = 10L;
        String newMemo = "Updated Memo";

        Enrollment enrollment = Enrollment.builder().userId(studentId).courseId(courseId).build();
        given(enrollmentRepository.findByCourseId(courseId)).willReturn(List.of(enrollment));

        // When
        courseService.updateStudentMemo(courseId, studentId, newMemo);

        // Then
        assertThat(enrollment.getMemo()).isEqualTo(newMemo);
    }

    @Test
    @DisplayName("수강생 메모 수정 실패 - 수강 이력 없음")
    void updateStudentMemo_Fail_NoEnrollment() {
        // Given
        Long courseId = 1L;
        Long studentId = 10L;

        given(enrollmentRepository.findByCourseId(courseId)).willReturn(List.of());

        // When & Then
        assertThatThrownBy(() -> courseService.updateStudentMemo(courseId, studentId, "Memo"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("수강생 정보를 찾을 수 없습니다");
    }
}
