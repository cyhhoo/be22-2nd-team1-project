
package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.CourseUpdateReqDTO;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseChangeRequest;
import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.entity.CourseType;
import com.mycompany.project.course.repository.CourseChangeRequestRepository;
import com.mycompany.project.course.repository.CourseRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
class CourseServiceFeaturesTest {

    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseChangeRequestRepository courseChangeRequestRepository;


/*
     * [성공 테스트]
     * 1. 강좌 개설 승인
     * 2. 강좌 개설 반려
     * 3. 강좌 변경 요청 (교사 변경) 및 승인
     */


    @Test
    @Transactional
    @DisplayName("[Success] 강좌 승인")
    void approveCourseSuccess() {
        // Given
        Course course = createPendingCourse();
        Long courseId = course.getId();

        // When
        courseService.approveCourse(courseId);

        // Then
        Course approvedCourse = courseRepository.findById(courseId).orElseThrow();
        assertThat(approvedCourse.getStatus()).isEqualTo(CourseStatus.OPEN);
    }

    @Test
    @Transactional
    @DisplayName("[Success] 강좌 반려 (사유 포함)")
    void refuseCourseSuccess() {
        // Given
        Course course = createPendingCourse();
        Long courseId = course.getId();
        String reason = "커리큘럼 부실";

        // When
        courseService.refuseCourse(courseId, reason);

        // Then
        Course refusedCourse = courseRepository.findById(courseId).orElseThrow();
        assertThat(refusedCourse.getStatus()).isEqualTo(CourseStatus.REFUSE);
        assertThat(refusedCourse.getRejectionReason()).isEqualTo(reason);
    }

    @Test
    @Transactional
    @DisplayName("[Success] 강좌 변경 요청 및 승인 (교사 변경)")
    void requestAndApproveUpdateSuccess() {
        // Given
        Course course = createOpenCourse();
        Long courseId = course.getId();
        Long originalTeacherId = course.getTeacherDetailId();
        Long newTeacherId = 999L;

        CourseUpdateReqDTO dto = new CourseUpdateReqDTO();
        dto.setTeacherDetailId(newTeacherId); // 교사 변경 요청

        // When (Request)
        Long requestId = courseService.requestCourseUpdate(courseId, dto, "담당 교사 변경");

        // Then (Verify Request)
        CourseChangeRequest request = courseChangeRequestRepository.findById(requestId).orElseThrow();
        assertThat(request.getTargetTeacherDetailId()).isEqualTo(newTeacherId);
        assertThat(request.getRequestStatus()).isEqualTo(CourseChangeRequest.RequestStatus.PENDING);

        // When (Approve)
        courseService.approveChangeRequest(requestId);

        // Then (Verify Approval)
        Course updatedCourse = courseRepository.findById(courseId).orElseThrow();
        assertThat(updatedCourse.getTeacherDetailId()).isEqualTo(newTeacherId);
        assertThat(updatedCourse.getTeacherDetailId()).isNotEqualTo(originalTeacherId);
    }


/*
     * [실패 테스트]
     * 1. 존재하지 않는 강좌 승인
     * 2. 이미 승인된 강좌 다시 승인
     * 3. 대기 상태가 아닌 요청 승인
     */


    @Test
    @DisplayName("[Failure] 존재하지 않는 강좌 승인 시 예외 발생")
    void approveNonExistentCourse() {
        assertThatThrownBy(() -> courseService.approveCourse(99999L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("존재하지 않는 강좌");
    }

    @Test
    @Transactional
    @DisplayName("[Failure] 이미 승인된(OPEN) 강좌 승인 시도 시 예외 발생")
    void approveAlreadyOpenCourse() {
        // Given
        Course course = createOpenCourse();

        // When & Then
        assertThatThrownBy(() -> courseService.approveCourse(course.getId()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("승인 대기 상태의 강좌만 승인할 수 있습니다");
    }

    @Test
    @Transactional
    @DisplayName("[Failure] 이미 승인된 요청을 다시 승인 시도")
    void approveAlreadyApprovedRequest() {
        // Given
        Course course = createOpenCourse();
        Long requestId = courseService.requestCourseUpdate(course.getId(), new CourseUpdateReqDTO(), "Test");
        courseService.approveChangeRequest(requestId); // 1차 승인

        // When & Then
        assertThatThrownBy(() -> courseService.approveChangeRequest(requestId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("대기 상태인 요청만 승인할 수 있습니다");
    }

    // Helper methods
    private Course createPendingCourse() {
        Course course = Course.builder()
                .name("Pending Course")
                .teacherDetailId(100L)
                .courseType(CourseType.MANDATORY)
                .maxCapacity(30)
                .status(CourseStatus.PENDING) // PENDING directly via Builder
                .build();
        return courseRepository.save(course);
    }

    private Course createOpenCourse() {
        Course course = Course.builder()
                .name("Open Course")
                .teacherDetailId(100L)
                .courseType(CourseType.MANDATORY)
                .maxCapacity(30)
                .status(CourseStatus.OPEN) // OPEN directly via Builder for test setup
                .build();
        return courseRepository.save(course);
    }
}

