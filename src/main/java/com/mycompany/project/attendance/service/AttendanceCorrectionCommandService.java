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
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AttendanceCorrectionCommandService {

    // 정정요청 저장/조회(JPA) - 중복요청 체크, 승인/반려 상태 변경 저장
    private final AttendanceCorrectionRequestRepository correctionRequestRepository;

    // 출결 저장/조회(JPA) - 정정요청 생성 시 출결 상태 확인, 승인 시 출결코드 반영
    private final AttendanceRepository attendanceRepository;

    // 출결 코드 조회(JPA) - 요청한 출결 코드가 존재/활성인지 검증
    private final AttendanceCodeRepository attendanceCodeRepository;

    // 사용자 조회(JPA) - 교사/관리자 권한 검사
    private final UserRepository userRepository;

    // 수강신청 조회(JPA) - 요청 출결이 어떤 과목(enrollment->course) 소속인지 확인
    private final EnrollmentRepository enrollmentRepository;

    // 강좌 조회(JPA) - 과목 담당 교사인지 확인
    private final CourseRepository courseRepository;

    /**
     * 정정요청 생성
     * - 교사가 "확정/마감된 출결"에 대해서만 정정요청을 올릴 수 있다.
     * - 동일 attendanceId에 대해 PENDING 요청이 있으면 중복 생성 불가.
     */
    @Transactional
    public void createCorrectionRequest(CorrectionCreateRequest request) {

        // 요청 DTO 필수값 검증
        validateCreateRequest(request);

        // 권한 체크: 교사만 생성 가능
        ensureTeacher(request.getRequestedBy());

        // 출결 존재 확인
        Attendance attendance = attendanceRepository.findById(request.getAttendanceId())
                .orElseThrow(() -> new BusinessException(ErrorCode.ATTENDANCE_NOT_FOUND));

        // ✅ 여기 로직은 메시지랑 조건이 살짝 헷갈릴 수 있음
        // - "확정 또는 마감된 출결만 가능"이라면 SAVED는 금지
        // - 즉, SAVED면 막는 게 맞다.
        if (attendance.getState() == AttendanceState.SAVED) {
            throw new BusinessException(ErrorCode.CORRECTION_ONLY_CONFIRMED_OR_CLOSED_ALLOWED);
        }

        // 과목 담당 교사인지 검증(출결 -> enrollment -> course -> teacher)
        ensureCourseTeacher(request.getRequestedBy(), attendance.getEnrollmentId());

        // 동일 출결에 대해 처리중(PENDING) 요청이 있으면 중복 생성 불가
        boolean exists = correctionRequestRepository.existsByAttendanceIdAndStatus(
                request.getAttendanceId(), CorrectionStatus.PENDING
        );
        if (exists) {
            throw new BusinessException(ErrorCode.CORRECTION_ALREADY_IN_PROGRESS);
        }

        // 요청한 출결 코드가 존재하고, 활성 상태인지 확인
        AttendanceCode requestedCode = attendanceCodeRepository.findById(request.getRequestedAttendanceCodeId())
                .orElseThrow(() -> new BusinessException(ErrorCode.REQUESTED_ATTENDANCE_CODE_NOT_FOUND));
        if (!requestedCode.isActive()) {
            throw new BusinessException(ErrorCode.ATTENDANCE_CODE_INACTIVE);
        }

        // 정정요청 엔티티 생성
        // - before: 현재 출결에 들어있는 코드
        // - requested: 바꾸고 싶은 코드
        AttendanceCorrectionRequest correctionRequest = new AttendanceCorrectionRequest(
                attendance.getId(),
                attendance.getAttendanceCodeId(),
                request.getRequestedAttendanceCodeId(),
                request.getRequestReason(),
                request.getRequestedBy()
        );

        // 정정요청 저장(PENDING 상태로 들어가는 구조가 보통)
        correctionRequestRepository.save(correctionRequest);
    }

    /**
     * 정정요청 처리(승인/반려)
     * - 관리자가 승인하면 출결 코드도 바로 반영한다.
     * - 반려면 사유(adminComment)가 필수다.
     */
    @Transactional
    public void decideCorrectionRequest(CorrectionDecideRequest request) {

        // 요청 DTO 필수값 검증
        validateDecideRequest(request);

        // 권한 체크: 관리자만 처리 가능
        ensureAdmin(request.getAdminId());

        // 정정요청 존재 확인
        AttendanceCorrectionRequest correctionRequest = correctionRequestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new BusinessException(ErrorCode.CORRECTION_REQUEST_NOT_FOUND));

        if (request.isApproved()) {
            // 승인 시 출결 반영과 상태 변경을 하나의 트랜잭션으로 처리한다.

            // 출결 존재 확인
            Attendance attendance = attendanceRepository.findById(correctionRequest.getAttendanceId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.ATTENDANCE_NOT_FOUND));

            // 정정요청 상태를 APPROVED로 변경 + 처리자/코멘트 기록
            correctionRequest.approve(request.getAdminId(), request.getAdminComment());

            // 출결에 요청 코드 반영(상태는 바꾸지 않고 코드만 변경)
            attendance.applyCorrection(correctionRequest.getRequestedAttendanceCodeId());

            // 출결 저장
            attendanceRepository.save(attendance);

        } else {
            // 반려면 사유 필수
            if (request.getAdminComment() == null || request.getAdminComment().isBlank()) {
                throw new BusinessException(ErrorCode.REJECT_REASON_REQUIRED);
            }

            // 정정요청 상태를 REJECTED로 변경 + 처리자/코멘트 기록
            correctionRequest.reject(request.getAdminId(), request.getAdminComment());
        }

        // 정정요청 저장(승인/반려 상태 반영)
        correctionRequestRepository.save(correctionRequest);
    }

    /**
     * 정정요청 생성 요청 필수값 검증
     */
    private void validateCreateRequest(CorrectionCreateRequest request) {
        if (request == null
                || request.getAttendanceId() == null
                || request.getRequestedAttendanceCodeId() == null
            || request.getRequestReason() == null
            || request.getRequestReason().isBlank()
            || request.getRequestedBy() == null) {
            throw new BusinessException(ErrorCode.ATTENDANCE_ITEM_INVALID_FORMAT);
        }
    }

    /**
     * 정정요청 처리 요청 필수값 검증
     */
    private void validateDecideRequest(CorrectionDecideRequest request) {
        if (request == null || request.getRequestId() == null || request.getAdminId() == null) {
            throw new BusinessException(ErrorCode.ATTENDANCE_STUDENT_LIST_EMPTY);
        }
    }

    /**
     * 관리자 권한 체크
     */
    private void ensureAdmin(Long adminId) {
        User user = userRepository.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != Role.ADMIN) {
            throw new BusinessException(ErrorCode.CORRECTION_ADMIN_ONLY);
        }
    }

    /**
     * 교사 권한 체크
     */
    private void ensureTeacher(Long teacherId) {
        User user = userRepository.findById(teacherId)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        if (user.getRole() != Role.TEACHER) {
            throw new BusinessException(ErrorCode.CORRECTION_TEACHER_ONLY_CREATE);
        }
    }

    /**
     * 과목 담당 교사인지 체크
     * - enrollment -> course를 따라가서, course.teacherDetailId == teacherId 인지 확인한다.
     */
    private void ensureCourseTeacher(Long teacherId, Long enrollmentId) {
        Enrollment enrollment = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ENROLLMENT_NOT_FOUND));

        Course course = courseRepository.findById(enrollment.getCourse().getCourseId())
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_INFO_NOT_FOUND));

        if (course.getTeacherDetail() == null || !course.getTeacherDetail().getId().equals(teacherId)) {
            throw new BusinessException(ErrorCode.CORRECTION_ONLY_COURSE_TEACHER_CREATE);
        }
    }
}
