package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.CourseUpdateReqDTO;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.course.repository.CourseChangeRequestRepository;
import com.mycompany.project.course.entity.CourseChangeRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CourseServiceApprovalTest {

    @Autowired
    private CourseRepository courseRepository;

    @Autowired
    private CourseChangeRequestRepository courseChangeRequestRepository;

    @Autowired
    private CourseService courseService;

    @Test
    @Transactional
    @DisplayName("강좌 반려 시 사유 저장 및 상태 변경 테스트")
    void refuseCourseWithReasonTest() {
        // 1. 강좌 생성 (PENDING)
        Course course = Course.builder().name("Refuse Test").maxCapacity(10).build();
        course = courseRepository.save(course);
        course.update(null, null, null, null, null, null, null, CourseStatus.PENDING);
        Long courseId = course.getId();

        String reason = "강의실 배정 문제로 반려";

        // 2. 반려 (사유 포함)
        courseService.refuseCourse(courseId, reason);

        // 3. 검증
        Course refusedCourse = courseRepository.findById(courseId).orElseThrow();
        assertEquals(CourseStatus.REFUSE, refusedCourse.getStatus());
        assertEquals(reason, refusedCourse.getRejectionReason());
    }

    @Test
    @Transactional
    @DisplayName("강좌 정보 변경 요청 및 승인 프로세스 테스트")
    void courseChangeRequestProcessTest() {
        // 1. 강좌 생성 (OPEN 상태로 가정)
        Course course = Course.builder()
                .name("Original Course")
                .maxCapacity(20)
                .tuition(100000)
                .build();
        course = courseRepository.save(course);
        Long courseId = course.getId();

        // 2. 변경 요청 생성
        CourseUpdateReqDTO updateDto = new CourseUpdateReqDTO();
        updateDto.setMaxCapacity(50); // 변경할 정원
        updateDto.setTuition(200000); // 변경할 수강료

        String requestReason = "수강생 증가로 인한 증원";

        Long requestId = courseService.requestCourseUpdate(courseId, updateDto, requestReason);

        // 3. 요청 생성 확인 (강좌 정보는 아직 변하지 않아야 함)
        Course originalCourse = courseRepository.findById(courseId).orElseThrow();
        assertEquals(20, originalCourse.getMaxCapacity()); // 변경 전
        assertEquals(100000, originalCourse.getTuition()); // 변경 전

        CourseChangeRequest request = courseChangeRequestRepository.findById(requestId).orElseThrow();
        assertEquals(CourseChangeRequest.RequestStatus.PENDING, request.getRequestStatus());
        assertEquals(requestReason, request.getReason());
        assertEquals(50, request.getTargetMaxCapacity());

        // 4. 변경 요청 승인
        courseService.approveChangeRequest(requestId);

        // 5. 승인 후 검증 (강좌 정보가 변경되어야 함)
        Course updatedCourse = courseRepository.findById(courseId).orElseThrow();
        assertEquals(50, updatedCourse.getMaxCapacity()); // 변경됨
        assertEquals(200000, updatedCourse.getTuition()); // 변경됨

        CourseChangeRequest approvedRequest = courseChangeRequestRepository.findById(requestId).orElseThrow();
        assertEquals(CourseChangeRequest.RequestStatus.APPROVED, approvedRequest.getRequestStatus());
    }

    @Test
    @Transactional
    @DisplayName("강좌 정보 변경 요청 반려 테스트")
    void rejectChangeRequestTest() {
        // 1. 강좌 생성
        Course course = Course.builder()
                .name("For Reject Test")
                .maxCapacity(20)
                .build();
        course = courseRepository.save(course);
        Long courseId = course.getId();

        // 2. 변경 요청 생성
        CourseUpdateReqDTO updateDto = new CourseUpdateReqDTO();
        updateDto.setMaxCapacity(100);

        Long requestId = courseService.requestCourseUpdate(courseId, updateDto, "무리한 증원 요청");

        // 3. 변경 요청 반려
        String rejectReason = "강의실 수용 불가";
        courseService.rejectChangeRequest(requestId, rejectReason);

        // 4. 검증 (강좌 정보 변하지 않아야 함)
        Course originalCourse = courseRepository.findById(courseId).orElseThrow();
        assertEquals(20, originalCourse.getMaxCapacity());

        CourseChangeRequest rejectedRequest = courseChangeRequestRepository.findById(requestId).orElseThrow();
        assertEquals(CourseChangeRequest.RequestStatus.REJECTED, rejectedRequest.getRequestStatus());
        assertEquals(rejectReason, rejectedRequest.getAdminComment());
    }
}