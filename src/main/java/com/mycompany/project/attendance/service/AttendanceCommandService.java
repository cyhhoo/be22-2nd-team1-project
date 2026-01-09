package com.mycompany.project.attendance.service;

import com.mycompany.project.attendance.dto.request.AttendanceConfirmRequest;
import com.mycompany.project.attendance.dto.request.AttendanceCreateRequest;
import com.mycompany.project.attendance.dto.request.AttendanceSearchRequest;
import com.mycompany.project.attendance.dto.request.AttendanceUpdateItemRequest;
import com.mycompany.project.attendance.dto.request.AttendanceUpdateRequest;
import com.mycompany.project.attendance.dto.response.AttendanceListResponse;
import com.mycompany.project.attendance.entity.Attendance;
import com.mycompany.project.attendance.entity.AttendanceCode;
import com.mycompany.project.attendance.entity.enums.AttendanceState;
import com.mycompany.project.attendance.repository.AttendanceCodeRepository;
import com.mycompany.project.attendance.repository.AttendanceRepository;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.user.entity.TeacherDetail;
import com.mycompany.project.user.repository.StudentDetailRepository;
import com.mycompany.project.user.repository.TeacherDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceCommandService {

    private static final String ENROLLMENT_STATUS_APPLIED = "APPLIED";
    private static final String DEFAULT_ATTENDANCE_CODE = "PRESENT";

    private final AttendanceRepository attendanceRepository;
    private final AttendanceCodeRepository attendanceCodeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final AttendanceQueryService attendanceQueryService;
    private final TeacherDetailRepository teacherDetailRepository;
    private final StudentDetailRepository studentDetailRepository;

    @Transactional
    public List<AttendanceListResponse> generateAttendances(AttendanceCreateRequest request) {
        validateGenerateRequest(request);

        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과목입니다."));
        ensureCourseTeacher(request.getUserId(), course);

        AttendanceCode defaultCode = attendanceCodeRepository.findByCodeAndActiveTrue(DEFAULT_ATTENDANCE_CODE)
            .orElseThrow(() -> new IllegalArgumentException("기본 출결 코드(PRESENT)를 찾을 수 없습니다."));

        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdAndStatus(
            request.getCourseId(), ENROLLMENT_STATUS_APPLIED
        );
        if (enrollments.isEmpty()) {
            return List.of();
        }

        // 이미 생성된 출결이 있으면 건너뛰고, 없는 것만 생성한다.
        List<Attendance> toSave = new ArrayList<>();
        byte period = request.getPeriod().byteValue();
        for (Enrollment enrollment : enrollments) {
            Optional<Attendance> existing = attendanceRepository.findByEnrollmentIdAndClassDateAndPeriod(
                enrollment.getEnrollmentId(), request.getClassDate(), period
            );
            if (existing.isEmpty()) {
                Attendance attendance = new Attendance(
                    request.getClassDate(),
                    period,
                    defaultCode.getId(),
                    enrollment.getEnrollmentId(),
                    request.getUserId(),
                    null
                );
                toSave.add(attendance);
            }
        }
        if (!toSave.isEmpty()) {
            attendanceRepository.saveAll(toSave);
        }

        // 생성 후에는 조회 API와 동일한 결과를 반환한다.
        AttendanceSearchRequest searchRequest = AttendanceSearchRequest.builder()
            .courseId(request.getCourseId())
            .fromDate(request.getClassDate())
            .toDate(request.getClassDate())
            .period(request.getPeriod())
            .build();
        return attendanceQueryService.search(searchRequest);
    }

    @Transactional
    public void saveAttendances(AttendanceUpdateRequest request) {
        validateSaveRequest(request);

        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과목입니다."));
        ensureCourseTeacher(request.getUserId(), course);

        // 과목에 등록된 수강신청 기준으로 요청 항목을 검증한다.
        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdAndStatus(
            request.getCourseId(), ENROLLMENT_STATUS_APPLIED
        );
        List<Long> enrollmentIds = enrollments.stream()
            .map(Enrollment::getEnrollmentId)
            .collect(Collectors.toList());

        byte period = request.getPeriod().byteValue();
        List<Attendance> toSave = new ArrayList<>();
        for (AttendanceUpdateItemRequest item : request.getItems()) {
            if (item.getEnrollmentId() == null || item.getAttendanceCodeId() == null) {
                throw new IllegalArgumentException("출결 항목 정보가 누락되었습니다.");
            }
            if (!enrollmentIds.contains(item.getEnrollmentId())) {
                throw new IllegalArgumentException("해당 과목의 수강 신청이 아닙니다.");
            }
            AttendanceCode code = attendanceCodeRepository.findById(item.getAttendanceCodeId())
                .orElseThrow(() -> new IllegalArgumentException("출결 코드가 존재하지 않습니다."));
            if (!code.isActive()) {
                throw new IllegalStateException("비활성화된 출결 코드는 사용할 수 없습니다.");
            }

            Optional<Attendance> existing = attendanceRepository.findByEnrollmentIdAndClassDateAndPeriod(
                item.getEnrollmentId(), request.getClassDate(), period
            );
            if (existing.isPresent()) {
                Attendance attendance = existing.get();
                if (attendance.getState() == AttendanceState.CONFIRMED || attendance.getState() == AttendanceState.CLOSED) {
                    throw new IllegalStateException("확정/마감 상태의 출결은 수정할 수 없습니다.");
                }
                attendance.saveByTeacher(request.getUserId(), code.getId(), item.getReason());
                toSave.add(attendance);
            } else {
                // 신규 출결은 저장 상태(SAVED)로 생성된다.
                Attendance attendance = new Attendance(
                    request.getClassDate(),
                    period,
                    code.getId(),
                    item.getEnrollmentId(),
                    request.getUserId(),
                    item.getReason()
                );
                toSave.add(attendance);
            }
        }
        if (!toSave.isEmpty()) {
            attendanceRepository.saveAll(toSave);
        }
    }

    @Transactional
    public void confirmAttendances(AttendanceConfirmRequest request) {
        validateConfirmRequest(request);

        Course course = courseRepository.findById(request.getCourseId())
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과목입니다."));
        ensureConfirmAuthority(request.getUserId(), course);

        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdAndStatus(
            request.getCourseId(), ENROLLMENT_STATUS_APPLIED
        );
        if (enrollments.isEmpty()) {
            throw new IllegalStateException("확정할 출결 대상이 없습니다.");
        }

        List<Long> enrollmentIds = enrollments.stream()
            .map(Enrollment::getEnrollmentId)
            .collect(Collectors.toList());

        // 미입력 출결이 있으면 확정 불가.
        long attendanceCount = attendanceRepository.countByEnrollmentIdInAndClassDateAndPeriod(
            enrollmentIds, request.getClassDate(), request.getPeriod().byteValue()
        );
        if (attendanceCount < enrollmentIds.size()) {
            throw new IllegalStateException("미입력 출결이 있어 확정할 수 없습니다.");
        }

        List<Attendance> attendances = attendanceRepository.findByEnrollmentIdInAndClassDateAndPeriod(
            enrollmentIds, request.getClassDate(), request.getPeriod().byteValue()
        );
        for (Attendance attendance : attendances) {
            if (attendance.getState() == AttendanceState.CLOSED) {
                throw new IllegalStateException("마감된 출결은 확정할 수 없습니다.");
            }
            if (attendance.getState() == AttendanceState.CONFIRMED) {
                continue;
            }
            attendance.confirm(request.getUserId());
        }
        attendanceRepository.saveAll(attendances);
    }

    private void validateGenerateRequest(AttendanceCreateRequest request) {
        if (request == null || request.getCourseId() == null || request.getClassDate() == null
            || request.getPeriod() == null || request.getUserId() == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }
    }

    private void validateSaveRequest(AttendanceUpdateRequest request) {
        if (request == null || request.getCourseId() == null || request.getClassDate() == null
            || request.getPeriod() == null || request.getUserId() == null || request.getItems() == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }
        if (request.getItems().isEmpty()) {
            throw new IllegalArgumentException("저장할 출결 항목이 없습니다.");
        }
    }

    private void validateConfirmRequest(AttendanceConfirmRequest request) {
        if (request == null || request.getCourseId() == null || request.getClassDate() == null
            || request.getPeriod() == null || request.getUserId() == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }
    }

    private void ensureCourseTeacher(Long userId, Course course) {
        if (!Objects.equals(course.getTeacherId(), userId)) {
            throw new IllegalStateException("과목 담당 교사만 처리할 수 있습니다.");
        }
    }

    private void ensureConfirmAuthority(Long userId, Course course) {
        if (Objects.equals(course.getTeacherId(), userId)) {
            return;
        }

        // 담당교사가 아니라면 담임 권한(학년/반 일치)을 확인한다.
        TeacherDetail teacherDetail = teacherDetailRepository.findById(userId)
            .orElseThrow(() -> new IllegalStateException("담임 권한이 없습니다."));
        if (teacherDetail.getHomeroomGrade() == null || teacherDetail.getHomeroomClassNo() == null) {
            throw new IllegalStateException("담임 권한이 없습니다.");
        }

        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdAndStatus(
            course.getCourseId(), ENROLLMENT_STATUS_APPLIED
        );
        if (enrollments.isEmpty()) {
            throw new IllegalStateException("확정할 출결 대상이 없습니다.");
        }

        List<Long> studentIds = enrollments.stream()
            .map(Enrollment::getStudentId)
            .collect(Collectors.toList());

        String classNo = String.valueOf(teacherDetail.getHomeroomClassNo());
        long matchedCount = studentDetailRepository.countByStudentIdInAndStudentGradeAndStudentClassNo(
            studentIds, teacherDetail.getHomeroomGrade(), classNo
        );
        if (matchedCount != studentIds.size()) {
            throw new IllegalStateException("담임 권한이 없습니다.");
        }
    }
}
