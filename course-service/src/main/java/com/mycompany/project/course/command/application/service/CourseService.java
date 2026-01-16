package com.mycompany.project.course.command.application.service;

import com.mycompany.project.course.client.EnrollmentClient;
import com.mycompany.project.course.client.InternalEnrollmentResponse;
import com.mycompany.project.course.client.UserClient;
import com.mycompany.project.course.command.application.dto.*;
import com.mycompany.project.course.command.domain.aggregate.*;
import com.mycompany.project.course.command.domain.repository.CourseChangeRequestRepository;
import com.mycompany.project.course.command.domain.repository.CourseRepository;
import com.mycompany.project.course.query.repository.CourseMapper;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.common.service.RefundService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {

    private final CourseRepository courseRepository;
    private final CourseChangeRequestRepository courseChangeRequestRepository;
    private final CourseMapper courseMapper;
    private final UserClient userClient;
    private final EnrollmentClient enrollmentClient;
    private final NotificationService notificationService;
    private final RefundService refundService;

    @Transactional
    public void createCourse(CourseCreateReqDTO dto) {
        if (dto.getMaxCapacity() == null || dto.getMaxCapacity() <= 0) {
            throw new BusinessException(ErrorCode.COURSE_BAD_REQUEST);
        }

        if (!userClient.existsByTeacherId(dto.getTeacherDetailId())) {
            throw new BusinessException(ErrorCode.INSTRUCTOR_NOT_FOUND);
        }

        Course course = Course.builder()
                .name(dto.getName())
                .courseType(dto.getCourseType())
                .maxCapacity(dto.getMaxCapacity())
                .tuition(dto.getTuition())
                .subjectId(dto.getSubjectId())
                .academicYearId(dto.getAcademicYearId())
                .teacherDetailId(dto.getTeacherDetailId())
                .status(CourseStatus.PENDING)
                .build();

        course = courseRepository.save(course);

        if (dto.getTimeSlots() != null) {
            for (TimeSlotDTO slotDto : dto.getTimeSlots()) {
                int teacherConflictCount = courseMapper.countTeacherSchedule(
                        dto.getAcademicYearId(),
                        dto.getTeacherDetailId(),
                        slotDto.getDayOfWeek(),
                        slotDto.getPeriod());

                if (teacherConflictCount > 0) {
                    throw new BusinessException(ErrorCode.INSTRUCTOR_TIMETABLE_CONFLICT);
                }

                int classroomConflictCount = courseMapper.countClassroomSchedule(
                        dto.getAcademicYearId(),
                        slotDto.getClassroom(),
                        slotDto.getDayOfWeek(),
                        slotDto.getPeriod());

                if (classroomConflictCount > 0) {
                    throw new BusinessException(ErrorCode.ALREADY_APPROVED_RESERVATION);
                }

                CourseTimeSlot timeSlot = CourseTimeSlot.builder()
                        .dayOfWeek(slotDto.getDayOfWeek())
                        .period(slotDto.getPeriod())
                        .classroom(slotDto.getClassroom())
                        .build();
                course.addTimeSlot(timeSlot);
            }
        }
        courseRepository.save(course);
    }

    @Transactional
    public void approveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getStatus() != CourseStatus.PENDING) {
            throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH);
        }

        course.update(null, null, null, null, null, null, null, CourseStatus.OPEN);
    }

    @Transactional
    public void refuseCourse(Long courseId, String reason) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (course.getStatus() != CourseStatus.PENDING) {
            throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH);
        }

        course.setRejectionReason(reason);
        course.update(null, null, null, null, null, null, null, CourseStatus.REFUSE);
    }

    @Transactional
    public void updateCourse(Long courseId, CourseUpdateReqDTO dto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (dto.getTeacherDetailId() != null && !userClient.existsByTeacherId(dto.getTeacherDetailId())) {
            throw new BusinessException(ErrorCode.INSTRUCTOR_NOT_FOUND);
        }

        course.update(
                dto.getName(),
                dto.getCourseType(),
                dto.getMaxCapacity(),
                dto.getTuition(),
                dto.getSubjectId(),
                dto.getAcademicYearId(),
                dto.getTeacherDetailId(),
                dto.getStatus());
    }

    @Transactional
    public Long requestCourseUpdate(Long courseId, CourseUpdateReqDTO dto, String reason) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        CourseChangeRequest request = CourseChangeRequest.builder()
                .course(course)
                .reason(reason)
                .targetMaxCapacity(dto.getMaxCapacity())
                .targetTuition(dto.getTuition())
                .targetTeacherDetailId(dto.getTeacherDetailId())
                .build();

        CourseChangeRequest savedRequest = courseChangeRequestRepository.save(request);
        return savedRequest.getId();
    }

    @Transactional
    public void approveChangeRequest(Long requestId) {
        CourseChangeRequest request = courseChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_CHANGE_REQUEST_NOT_FOUND));

        if (request.getRequestStatus() != CourseChangeRequest.RequestStatus.PENDING) {
            throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH);
        }

        Course course = request.getCourse();
        course.updateCourseInfo(
                null,
                null,
                request.getTargetMaxCapacity(),
                request.getTargetTuition(),
                null,
                null,
                request.getTargetTeacherDetailId());

        request.approve();
    }

    @Transactional
    public void rejectChangeRequest(Long requestId, String reason) {
        CourseChangeRequest request = courseChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_CHANGE_REQUEST_NOT_FOUND));

        if (request.getRequestStatus() != CourseChangeRequest.RequestStatus.PENDING) {
            throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH);
        }

        request.reject(reason);
    }

    @Transactional
    public void changeTeacher(Long courseId, Long newTeacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (!userClient.existsByTeacherId(newTeacherId)) {
            throw new BusinessException(ErrorCode.INSTRUCTOR_NOT_FOUND);
        }

        List<CourseTimeSlot> timeSlots = course.getTimeSlots();
        for (CourseTimeSlot slot : timeSlots) {
            int conflictCount = courseMapper.countTeacherSchedule(
                    course.getAcademicYearId(),
                    newTeacherId,
                    slot.getDayOfWeek(),
                    slot.getPeriod());

            if (conflictCount > 0) {
                throw new BusinessException(ErrorCode.INSTRUCTOR_TIMETABLE_CONFLICT);
            }
        }

        course.updateCourseInfo(null, null, null, null, null, null, newTeacherId);

        List<InternalEnrollmentResponse> enrollments = enrollmentClient.getEnrollmentsByCourse(courseId);
        List<Long> studentIds = enrollments.stream().map(InternalEnrollmentResponse::getStudentDetailId)
                .collect(Collectors.toList());
        notificationService.sendByIds(studentIds, "The assigned teacher has been changed.");
    }

    @Transactional
    public void deleteCourse(Long courseId, String reason) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        course.changeStatus(CourseStatus.CANCELED);

        List<InternalEnrollmentResponse> enrollments = enrollmentClient.getEnrollmentsByCourse(courseId);
        for (InternalEnrollmentResponse e : enrollments) {
            if ("APPLIED".equals(e.getStatus())) {
                refundService.processRefund(e.getStudentDetailId(), courseId, "Course canceled: " + reason);
            }
        }

        List<Long> studentIds = enrollments.stream().map(InternalEnrollmentResponse::getStudentDetailId)
                .collect(Collectors.toList());
        notificationService.sendByIds(studentIds,
                "Course(" + course.getName() + ") has been canceled. Reason: " + reason);
    }

    @Transactional
    public void forceCancelStudent(Long courseId, Long studentId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE);
        }
        // This feature is handled in enrollment-service
        // Calls enrollmentClient to send reason
    }

    @Transactional(readOnly = true)
    public StudentDetailResDTO getStudentDetail(Long courseId, Long studentId) {
        InternalEnrollmentResponse e = enrollmentClient.getStudentEnrollment(courseId, studentId);
        if (e == null) {
            throw new BusinessException(ErrorCode.COURSE_ENROLLMENT_NOT_FOUND);
        }

        return StudentDetailResDTO.builder()
                .studentId(studentId)
                .studentName("Student_" + studentId) // Placeholder
                .memo(e.getMemo())
                .attendancePresent(0)
                .attendanceLate(0)
                .attendanceAbsent(0)
                .assignmentTotal(0)
                .assignmentSubmitted(0)
                .build();
    }

    @Transactional
    public void updateStudentMemo(Long courseId, Long studentId, String memo) {
        enrollmentClient.updateEnrollmentMemo(courseId, studentId, memo);
    }

    @Transactional(readOnly = true)
    public Page<CourseListResDTO> getCourseList(Long teacherDetailId, Pageable pageable) {
        return courseRepository.findByTeacherDetailId(teacherDetailId, pageable)
                .map(this::convertToCourseListResDTO);
    }

    @Transactional(readOnly = true)
    public Page<CourseListResDTO> getAllCourses(Pageable pageable) {
        return courseRepository.findAll(pageable)
                .map(this::convertToCourseListResDTO);
    }

    private CourseListResDTO convertToCourseListResDTO(Course course) {
        return CourseListResDTO.builder()
                .courseId(course.getCourseId())
                .name(course.getName())
                .courseType(course.getCourseType())
                .status(course.getStatus())
                .currentCount(course.getCurrentCount())
                .maxCapacity(course.getMaxCapacity())
                .teacherName("Teacher_" + course.getTeacherDetailId())
                .build();
    }

    @Transactional
    public void changeCourseStatus(Long courseId, CourseStatus status) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));

        if (status != CourseStatus.OPEN && status != CourseStatus.CLOSED) {
            throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH);
        }

        course.changeStatus(status);
    }

    @Transactional(readOnly = true)
    public TeacherTimetableResDTO getTeacherTimetable(Long teacherDetailId, Long academicYearId) {
        List<Map<String, Object>> timeSlotsMap = courseMapper.findTeacherTimetable(academicYearId, teacherDetailId);

        List<TeacherTimetableResDTO.TimeSlotInfo> timeSlots = timeSlotsMap.stream()
                .map(m -> TeacherTimetableResDTO.TimeSlotInfo.builder()
                        .dayOfWeek((String) m.get("dayOfWeek"))
                        .period((Integer) m.get("period"))
                        .courseId((Long) m.get("courseId"))
                        .courseName((String) m.get("courseName"))
                        .classroom((String) m.get("classroom"))
                        .courseType(String.valueOf(m.get("courseType")))
                        .build())
                .collect(Collectors.toList());

        return TeacherTimetableResDTO.builder()
                .timeSlots(timeSlots)
                .build();
    }

    public InternalCourseResponse getInternalCourseInfo(Long courseId) {
        Course course = courseRepository.findById(courseId).orElse(null);
        if (course == null)
            return null;

        InternalCourseResponse response = new InternalCourseResponse();
        response.setCourseId(course.getCourseId());
        response.setName(course.getName());
        response.setTeacherDetailId(course.getTeacherDetailId());
        response.setAcademicYearId(course.getAcademicYearId());
        response.setSubjectId(course.getSubjectId());
        response.setCourseType(course.getCourseType());
        response.setStatus(course.getStatus());

        List<InternalCourseResponse.TimeSlotResponse> slots = course.getTimeSlots().stream()
                .map(ts -> {
                    InternalCourseResponse.TimeSlotResponse slot = new InternalCourseResponse.TimeSlotResponse();
                    slot.setDayOfWeek(ts.getDayOfWeek());
                    slot.setPeriod(ts.getPeriod());
                    return slot;
                }).collect(Collectors.toList());
        response.setTimeSlots(slots);

        return response;
    }

    @Transactional
    public void increaseEnrollment(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        course.increaseCurrentCount();
    }

    @Transactional
    public void decreaseEnrollment(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND));
        course.decreaseCurrentCount();
    }

    @Transactional(readOnly = true)
    public List<InternalCourseResponse> getInternalCoursesByAcademicYear(Long academicYearId) {
        return courseRepository.findByAcademicYearId(academicYearId).stream()
                .map(course -> {
                    InternalCourseResponse response = new InternalCourseResponse();
                    response.setCourseId(course.getCourseId());
                    response.setName(course.getName());
                    response.setTeacherDetailId(course.getTeacherDetailId());
                    response.setAcademicYearId(course.getAcademicYearId());
                    response.setSubjectId(course.getSubjectId());
                    response.setCourseType(course.getCourseType());
                    response.setStatus(course.getStatus());
                    return response;
                })
                .collect(Collectors.toList());
    }
}
