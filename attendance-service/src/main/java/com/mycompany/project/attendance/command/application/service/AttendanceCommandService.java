package com.mycompany.project.attendance.command.application.service;

import com.mycompany.project.attendance.command.application.dto.AttendanceConfirmRequest;
import com.mycompany.project.attendance.command.application.dto.AttendanceCreateRequest;
import com.mycompany.project.attendance.command.application.dto.AttendanceSearchRequest;
import com.mycompany.project.attendance.command.application.dto.AttendanceUpdateItemRequest;
import com.mycompany.project.attendance.command.application.dto.AttendanceUpdateRequest;
import com.mycompany.project.attendance.query.application.dto.AttendanceListResponse;
import com.mycompany.project.attendance.client.CourseClient;
import com.mycompany.project.attendance.client.EnrollmentClient;
import com.mycompany.project.attendance.client.UserClient;
import com.mycompany.project.attendance.client.dto.InternalCourseResponse;
import com.mycompany.project.attendance.client.dto.InternalEnrollmentResponse;
import com.mycompany.project.common.dto.InternalTeacherResponse;
import com.mycompany.project.attendance.command.domain.aggregate.Attendance;
import com.mycompany.project.attendance.command.domain.aggregate.AttendanceCode;
import com.mycompany.project.attendance.command.domain.aggregate.enums.AttendanceState;
import com.mycompany.project.attendance.command.domain.repository.AttendanceCodeRepository;
import com.mycompany.project.attendance.command.domain.repository.AttendanceRepository;
import com.mycompany.project.common.enums.EnrollmentStatus;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
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

    // Only APPLIED enrollment status students are attendance targets
    private static final EnrollmentStatus ENROLLMENT_STATUS_APPLIED = EnrollmentStatus.APPLIED;

    // Default attendance code for auto-generation (PRESENT)
    private static final String DEFAULT_ATTENDANCE_CODE = "PRESENT";

    // Attendance data operation (JPA)
    private final AttendanceRepository attendanceRepository;

    // Attendance code lookup (JPA)
    private final AttendanceCodeRepository attendanceCodeRepository;

    // External Service Clients (Feign)
    private final EnrollmentClient enrollmentClient;
    private final CourseClient courseClient;
    private final UserClient userClient;

    // Query module (MyBatis)
    private final com.mycompany.project.attendance.query.application.service.AttendanceQueryService attendanceQueryService;

    /**
     * Auto-generate attendance sheet (create if not exists + query if exists)
     * - Logic for when course teacher first opens attendance for a specific
     * date/period
     * - Creates attendance records for each student based on enrollment
     */
    @Transactional
    public List<AttendanceListResponse> generateAttendances(AttendanceCreateRequest request) {

        // Validate required fields
        validateGenerateRequest(request);

        // Verify course exists
        InternalCourseResponse course = courseClient.getInternalCourseInfo(request.getCourseId());
        if (course == null) {
            throw new BusinessException(ErrorCode.ATT_COURSE_NOT_FOUND);
        }

        // Permission check: only course teacher can create
        ensureCourseTeacher(request.getUserId(), course);

        // Get default attendance code (PRESENT) - only active codes
        AttendanceCode defaultCode = attendanceCodeRepository.findByCodeAndActiveTrue(DEFAULT_ATTENDANCE_CODE)
                .orElseThrow(() -> new BusinessException(ErrorCode.DEFAULT_ATTENDANCE_CODE_NOT_FOUND));

        // Get enrolled students for this course (APPLIED status)
        List<InternalEnrollmentResponse> enrollments = enrollmentClient.getInternalEnrollments(
                request.getCourseId(), ENROLLMENT_STATUS_APPLIED.name());
        if (enrollments.isEmpty()) {
            // No enrolled students means no attendance to create
            return List.of();
        }

        // Skip if already exists, create only new ones
        List<Attendance> toSave = new ArrayList<>();
        byte period = request.getPeriod().byteValue();

        for (InternalEnrollmentResponse enrollment : enrollments) {

            // Check if attendance already exists (enrollmentId + date + period)
            Optional<Attendance> existing = attendanceRepository.findByEnrollmentIdAndClassDateAndPeriod(
                    enrollment.getEnrollmentId(), request.getClassDate(), period);

            if (existing.isEmpty()) {
                // Create new attendance with default code (PRESENT)
                Attendance attendance = new Attendance(
                        request.getClassDate(),
                        period,
                        defaultCode.getId(),
                        enrollment.getEnrollmentId(),
                        request.getUserId(),
                        null // reason is empty on first creation
                );
                toSave.add(attendance);
            }
        }

        // Save all new records at once
        if (!toSave.isEmpty()) {
            attendanceRepository.saveAll(toSave);
        }

        // Return same format as query API (includes newly created)
        AttendanceSearchRequest searchRequest = AttendanceSearchRequest.builder()
                .courseId(request.getCourseId())
                .fromDate(request.getClassDate())
                .toDate(request.getClassDate())
                .period(request.getPeriod())
                .build();

        return attendanceQueryService.search(searchRequest);
    }

    /**
     * Save attendance (register/modify)
     * - Teacher saves attendance code/reason
     * - Cannot modify CONFIRMED/CLOSED status
     */
    @Transactional
    public void saveAttendances(AttendanceUpdateRequest request) {

        // Validate required fields + items
        validateSaveRequest(request);

        // Verify course exists
        InternalCourseResponse course = courseClient.getInternalCourseInfo(request.getCourseId());
        if (course == null) {
            throw new BusinessException(ErrorCode.ATT_COURSE_NOT_FOUND);
        }

        // Permission check: only course teacher can save
        ensureCourseTeacher(request.getUserId(), course);

        // Get enrolled students to verify request enrollmentIds
        List<InternalEnrollmentResponse> enrollments = enrollmentClient.getInternalEnrollments(
                request.getCourseId(), ENROLLMENT_STATUS_APPLIED.name());

        List<Long> enrollmentIds = enrollments.stream()
                .map(InternalEnrollmentResponse::getEnrollmentId)
                .collect(Collectors.toList());

        byte period = request.getPeriod().byteValue();

        // Collect entities to save
        List<Attendance> toSave = new ArrayList<>();

        for (AttendanceUpdateItemRequest item : request.getItems()) {

            // Validate required item fields
            if (item.getEnrollmentId() == null || item.getAttendanceCodeId() == null) {
                throw new BusinessException(ErrorCode.ATTENDANCE_ITEM_INFO_MISSING);
            }

            // Verify enrollmentId belongs to this course
            if (!enrollmentIds.contains(item.getEnrollmentId())) {
                throw new BusinessException(ErrorCode.NOT_ENROLLED_IN_COURSE);
            }

            // Verify attendance code exists and is active
            Long attendanceCodeId = item.getAttendanceCodeId();
            AttendanceCode code = attendanceCodeRepository.findById(java.util.Objects.requireNonNull(attendanceCodeId))
                    .orElseThrow(() -> new BusinessException(ErrorCode.ATTENDANCE_CODE_NOT_FOUND));

            if (!code.isActive()) {
                throw new BusinessException(ErrorCode.ATTENDANCE_CODE_INACTIVE);
            }

            // Check if existing attendance
            Optional<Attendance> existing = attendanceRepository.findByEnrollmentIdAndClassDateAndPeriod(
                    item.getEnrollmentId(), request.getClassDate(), period);

            if (existing.isPresent()) {
                // Update existing
                Attendance attendance = existing.get();

                // Cannot modify CONFIRMED/CLOSED
                if (attendance.getState() == AttendanceState.CONFIRMED
                        || attendance.getState() == AttendanceState.CLOSED) {
                    throw new BusinessException(ErrorCode.ATTENDANCE_CANNOT_MODIFY_CONFIRMED_OR_CLOSED);
                }

                // Update via business method (code/reason/modifier/time)
                attendance.saveByTeacher(request.getUserId(), code.getId(), item.getReason());
                toSave.add(attendance);

            } else {
                // Create new attendance (state=SAVED)
                Attendance attendance = new Attendance(
                        request.getClassDate(),
                        period,
                        code.getId(),
                        item.getEnrollmentId(),
                        request.getUserId(),
                        item.getReason());
                toSave.add(attendance);
            }
        }

        // Save all at once
        if (!toSave.isEmpty()) {
            attendanceRepository.saveAll(toSave);
        }
    }

    /**
     * Confirm attendance (CONFIRMED)
     * - Only homeroom teacher can confirm
     * - Cannot confirm if there are missing attendance items
     */
    @Transactional
    public void confirmAttendances(AttendanceConfirmRequest request) {

        // Validate required fields
        validateConfirmRequest(request);

        // Verify course exists
        InternalCourseResponse course = courseClient.getInternalCourseInfo(request.getCourseId());
        if (course == null) {
            throw new BusinessException(ErrorCode.ATT_COURSE_NOT_FOUND);
        }

        // Permission check: verify homeroom teacher authority
        ensureConfirmAuthority(request.getUserId(), course);

        // Get enrolled students (APPLIED)
        List<InternalEnrollmentResponse> enrollments = enrollmentClient.getInternalEnrollments(
                request.getCourseId(), ENROLLMENT_STATUS_APPLIED.name());

        if (enrollments.isEmpty()) {
            throw new BusinessException(ErrorCode.ATTENDANCE_NOTHING_TO_CONFIRM);
        }

        // Extract enrollmentId list
        List<Long> enrollmentIds = enrollments.stream()
                .map(InternalEnrollmentResponse::getEnrollmentId)
                .collect(Collectors.toList());

        // Check for missing attendance items
        long attendanceCount = attendanceRepository.countByEnrollmentIdInAndClassDateAndPeriod(
                enrollmentIds, request.getClassDate(), request.getPeriod().byteValue());

        if (attendanceCount < enrollmentIds.size()) {
            throw new BusinessException(ErrorCode.ATTENDANCE_CANNOT_CONFIRM_WITH_UNFILLED_ITEMS);
        }

        // Get all attendance records
        List<Attendance> attendances = attendanceRepository.findByEnrollmentIdInAndClassDateAndPeriod(
                enrollmentIds, request.getClassDate(), request.getPeriod().byteValue());

        // Confirm each attendance
        for (Attendance attendance : attendances) {

            // Cannot confirm CLOSED
            if (attendance.getState() == AttendanceState.CLOSED) {
                throw new BusinessException(ErrorCode.ATTENDANCE_CANNOT_CONFIRM_CLOSED);
            }

            // Skip if already CONFIRMED
            if (attendance.getState() == AttendanceState.CONFIRMED) {
                continue;
            }

            // Confirm via business method
            attendance.confirm(request.getUserId());
        }

        // Save all changes
        if (!attendances.isEmpty()) {
            attendanceRepository.saveAll(attendances);
        }
    }

    // ====== Request DTO validation methods ======

    // Validate attendance generation request
    private void validateGenerateRequest(AttendanceCreateRequest request) {
        if (request == null
                || request.getCourseId() == null
                || request.getClassDate() == null
                || request.getPeriod() == null
                || request.getUserId() == null) {
            throw new BusinessException(ErrorCode.REQUIRED_PARAMETER_MISSING);
        }
    }

    // Validate attendance save request
    private void validateSaveRequest(AttendanceUpdateRequest request) {
        if (request == null
                || request.getCourseId() == null
                || request.getClassDate() == null
                || request.getPeriod() == null
                || request.getUserId() == null
                || request.getItems() == null) {
            throw new BusinessException(ErrorCode.ATTENDANCE_REQUIRED_PARAMS_MISSING);
        }
        if (request.getItems().isEmpty()) {
            throw new BusinessException(ErrorCode.ATTENDANCE_ITEMS_EMPTY);
        }
    }

    // Validate attendance confirm request
    private void validateConfirmRequest(AttendanceConfirmRequest request) {
        if (request == null
                || request.getCourseId() == null
                || request.getClassDate() == null
                || request.getPeriod() == null
                || request.getUserId() == null) {
            throw new BusinessException(ErrorCode.REQUIRED_PARAMETER_MISSING);
        }
    }

    // ====== Permission check methods ======

    /**
     * Check course teacher permission
     * - Course's assigned teacher (teacherDetailId) must match request userId
     */
    private void ensureCourseTeacher(Long userId, InternalCourseResponse course) {
        if (!Objects.equals(course.getTeacherDetailId(), userId)) {
            throw new BusinessException(ErrorCode.ONLY_COURSE_TEACHER_ALLOWED);
        }
    }

    /**
     * Check confirm authority
     * - Verify homeroom teacher authority (grade/class match)
     */
    private void ensureConfirmAuthority(Long userId, InternalCourseResponse course) {

        // Check homeroom teacher authority
        InternalTeacherResponse teacherDetail = userClient.getTeacherInfo(userId);
        if (teacherDetail == null) {
            throw new BusinessException(ErrorCode.HOMEROOM_PERMISSION_REQUIRED);
        }

        if (teacherDetail.getHomeroomGrade() == null || teacherDetail.getHomeroomClassNo() == null) {
            throw new BusinessException(ErrorCode.HOMEROOM_PERMISSION_REQUIRED);
        }

        // Get enrolled students
        List<InternalEnrollmentResponse> enrollments = enrollmentClient.getInternalEnrollments(
                course.getCourseId(), ENROLLMENT_STATUS_APPLIED.name());

        if (enrollments.isEmpty()) {
            throw new BusinessException(ErrorCode.ATTENDANCE_NOTHING_TO_CONFIRM);
        }

        // Extract studentDetailIds
        List<Long> studentDetailIds = enrollments.stream()
                .map(InternalEnrollmentResponse::getStudentDetailId)
                .collect(Collectors.toList());

        // Verify all students match homeroom grade/class
        String classNo = String.valueOf(teacherDetail.getHomeroomClassNo());

        Long matchedCount = userClient.countMatchedStudents(
                studentDetailIds, teacherDetail.getHomeroomGrade(), classNo);

        // If not all matched, not the homeroom teacher
        if (matchedCount == null || matchedCount != studentDetailIds.size()) {
            throw new BusinessException(ErrorCode.HOMEROOM_PERMISSION_REQUIRED);
        }
    }
}
