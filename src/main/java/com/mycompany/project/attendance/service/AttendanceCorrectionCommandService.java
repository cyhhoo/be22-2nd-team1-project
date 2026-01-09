package com.mycompany.project.attendance.service;

import com.mycompany.project.attendance.dto.request.CorrectionCreateRequest;
import com.mycompany.project.attendance.dto.request.CorrectionDecideRequest;
import com.mycompany.project.attendance.entity.Attendance;
import com.mycompany.project.attendance.entity.AttendanceCorrectionRequest;
import com.mycompany.project.attendance.entity.AttendanceCode;
import com.mycompany.project.attendance.entity.enums.AttendanceState;
import com.mycompany.project.attendance.entity.enums.CorrectionStatus;
import com.mycompany.project.attendance.repository.AttendanceCodeRepository;
import com.mycompany.project.attendance.repository.AttendanceCorrectionRequestRepository;
import com.mycompany.project.attendance.repository.AttendanceRepository;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.user.entity.Role;
import com.mycompany.project.user.entity.User;
import com.mycompany.project.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceCorrectionCommandService {

    private final AttendanceCorrectionRequestRepository correctionRequestRepository;
    private final AttendanceRepository attendanceRepository;
    private final AttendanceCodeRepository attendanceCodeRepository;
    private final UserRepository userRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;

    @Transactional
    public void createCorrectionRequest(CorrectionCreateRequest request) {
        validateCreateRequest(request);
        ensureTeacher(request.getRequestedBy());

        Attendance attendance = attendanceRepository.findById(request.getAttendanceId())
            .orElseThrow(() -> new IllegalArgumentException("출결 정보가 존재하지 않습니다."));
        if (attendance.getState() == AttendanceState.SAVED) {
            throw new IllegalStateException("확정 또는 마감된 출결만 정정요청이 가능합니다.");
        }

        ensureCourseTeacher(request.getRequestedBy(), attendance.getEnrollmentId());

        // 동일 출결에 대해 처리중(PENDING) 요청이 있으면 중복 생성 불가.
        boolean exists = correctionRequestRepository.existsByAttendanceIdAndStatus(
            request.getAttendanceId(), CorrectionStatus.PENDING
        );
        if (exists) {
            throw new IllegalStateException("이미 처리 중인 정정요청이 있습니다.");
        }

        attendanceCodeRepository.findById(request.getRequestedAttendanceCodeId())
            .filter(AttendanceCode::isActive)
            .orElseThrow(() -> new IllegalArgumentException("요청 출결코드가 존재하지 않습니다."));

        AttendanceCorrectionRequest correctionRequest = new AttendanceCorrectionRequest(
            attendance.getId(),
            attendance.getAttendanceCodeId(),
            request.getRequestedAttendanceCodeId(),
            request.getRequestReason(),
            request.getRequestedBy()
        );
        correctionRequestRepository.save(correctionRequest);
    }

    @Transactional
    public void decideCorrectionRequest(CorrectionDecideRequest request) {
        validateDecideRequest(request);
        ensureAdmin(request.getAdminId());

        AttendanceCorrectionRequest correctionRequest = correctionRequestRepository.findById(request.getRequestId())
            .orElseThrow(() -> new IllegalArgumentException("정정요청이 존재하지 않습니다."));

        if (request.isApproved()) {
            // 승인 시 출결 반영과 상태 변경을 하나의 트랜잭션으로 처리한다.
            Attendance attendance = attendanceRepository.findById(correctionRequest.getAttendanceId())
                .orElseThrow(() -> new IllegalArgumentException("출결 정보가 존재하지 않습니다."));
            correctionRequest.approve(request.getAdminId(), request.getAdminComment());
            attendance.applyCorrection(correctionRequest.getRequestedAttendanceCodeId());
            attendanceRepository.save(attendance);
        } else {
            if (request.getAdminComment() == null || request.getAdminComment().isBlank()) {
                throw new IllegalArgumentException("반려 사유는 필수입니다.");
            }
            correctionRequest.reject(request.getAdminId(), request.getAdminComment());
        }
        correctionRequestRepository.save(correctionRequest);
    }

    private void validateCreateRequest(CorrectionCreateRequest request) {
        if (request == null || request.getAttendanceId() == null || request.getRequestedAttendanceCodeId() == null
            || request.getRequestReason() == null || request.getRequestReason().isBlank() || request.getRequestedBy() == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }
    }

    private void validateDecideRequest(CorrectionDecideRequest request) {
        if (request == null || request.getRequestId() == null || request.getAdminId() == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }
    }

    private void ensureAdmin(Long adminId) {
        User user = userRepository.findById(adminId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("관리자만 처리할 수 있습니다.");
        }
    }

    private void ensureTeacher(Long teacherId) {
        User user = userRepository.findById(teacherId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (user.getRole() != Role.TEACHER) {
            throw new IllegalStateException("교사만 정정요청을 생성할 수 있습니다.");
        }
    }

    private void ensureCourseTeacher(Long teacherId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
            .orElseThrow(() -> new IllegalArgumentException("수강신청 정보가 없습니다."));
        Course course = courseRepository.findById(enrollment.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("과목 정보가 없습니다."));
        if (course.getTeacherId() == null || !course.getTeacherId().equals(teacherId)) {
            throw new IllegalStateException("과목 담당 교사만 정정요청을 생성할 수 있습니다.");
        }
    }
}
