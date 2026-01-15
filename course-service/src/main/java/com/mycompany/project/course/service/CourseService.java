package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.TimeSlotDTO;
import com.mycompany.project.course.dto.CourseCreateReqDTO;
import com.mycompany.project.course.dto.StudentDetailResDTO;
import com.mycompany.project.course.repository.CourseChangeRequestRepository;
import com.mycompany.project.course.entity.*;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.entity.EnrollmentStatus;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import com.mycompany.project.user.command.domain.aggregate.StudentDetail;
import com.mycompany.project.user.command.domain.repository.StudentDetailRepository;
import com.mycompany.project.user.command.domain.repository.TeacherDetailRepository;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

import com.mycompany.project.course.mapper.CourseMapper;

import org.springframework.transaction.annotation.Transactional;
import com.mycompany.project.course.dto.CourseListResDTO;
import com.mycompany.project.course.dto.CourseUpdateReqDTO;
import com.mycompany.project.course.dto.InternalCourseResponse;
import com.mycompany.project.course.entity.*;

import com.mycompany.project.common.service.RefundService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseService {
    /*
     * [Architecture Note]
     * - Command (CUD): Use JPA (CourseRepository) for domain entity state changes.
     * - Query (Read): Use MyBatis (CourseMapper) for complex reads and duplicate
     * checks.
     */
    private final CourseRepository courseRepository;
    private final CourseChangeRequestRepository courseChangeRequestRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;
    private final UserRepository userRepository;
    private final NotificationService notificationService;
    private final RefundService refundService;
    private final StudentDetailRepository studentDetailRepository;
    private final TeacherDetailRepository teacherDetailRepository;

    /**
     * 강좌 개설 신청 (상태: PENDING)
     */
    @Transactional
    public void createCourse(CourseCreateReqDTO dto) {
        // 1. 유효성 검증
        if (dto.getMaxCapacity() == null || dto.getMaxCapacity() <= 0) {
            throw new BusinessException(ErrorCode.COURSE_BAD_REQUEST); // 수강 정원은 필수이며 0보다 커야 합니다.
        }

        // 2. 강좌 엔티티 생성 (Status = PENDING)
        Course course = Course.builder()
                .name(dto.getName())
                .courseType(dto.getCourseType())
                .maxCapacity(dto.getMaxCapacity())
                .tuition(dto.getTuition())
                .subjectId(dto.getSubjectId())
                .academicYearId(dto.getAcademicYearId())
                .teacherDetail(teacherDetailRepository.getReferenceById(dto.getTeacherDetailId()))
                .status(CourseStatus.PENDING) // 초기 상태 PENDING 설정
                .build();

        // Course 저장 (JPA: INSERT)
        course = courseRepository.save(course);

        // 3. 시간표(TimeSlot) 저장 및 중복 검증 (MyBatis 사용)
        if (dto.getTimeSlots() != null) {
            for (TimeSlotDTO slotDto : dto.getTimeSlots()) {
                // 교사 중복 검증 (MyBatis)
                int teacherConflictCount = courseMapper.countTeacherSchedule(
                        dto.getAcademicYearId(),
                        dto.getTeacherDetailId(),
                        slotDto.getDayOfWeek(),
                        slotDto.getPeriod());

                if (teacherConflictCount > 0) {
                    throw new BusinessException(ErrorCode.INSTRUCTOR_TIMETABLE_CONFLICT); // 해당 시간에 담당 교사의 다른 수업이 존재합니다.
                }

                // 강의실 중복 검증 (MyBatis)
                int classroomConflictCount = courseMapper.countClassroomSchedule(
                        dto.getAcademicYearId(),
                        slotDto.getClassroom(),
                        slotDto.getDayOfWeek(),
                        slotDto.getPeriod());

                if (classroomConflictCount > 0) {
                    throw new BusinessException(ErrorCode.ALREADY_APPROVED_RESERVATION); // 해당 시간에 강의실이 이미 사용 중입니다.
                }

                // 시간표 엔티티 생성 및 추가
                CourseTimeSlot timeSlot = CourseTimeSlot.builder()
                        .dayOfWeek(slotDto.getDayOfWeek())
                        .period(slotDto.getPeriod())
                        .classroom(slotDto.getClassroom())
                        .build();
                course.addTimeSlot(timeSlot); // 연관관계 매핑
            }
        }
        // Cascade.ALL로 인해 timeSlots도 자동 저장됨 (확실한 저장을 위해 save 호출)
        courseRepository.save(course);
    }

    /**
     * 강좌 승인 (PENDING -> OPEN)
     */
    @Transactional
    public void approveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND)); // 존재하지 않는 강좌입니다.

        if (course.getStatus() != CourseStatus.PENDING) {
            throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH); // 승인 대기 상태의 강좌만 승인할 수 있습니다.
        }

        course.update(null, null, null, null, null, null, null, CourseStatus.OPEN);
    }

    /**
     * 강좌 반려 (PENDING -> REFUSE)
     * 
     * @param courseId 강좌 ID
     * @param reason   반려 사유
     */
    @Transactional
    public void refuseCourse(Long courseId, String reason) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND)); // 존재하지 않는 강좌입니다.

        if (course.getStatus() != CourseStatus.PENDING) {
            throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH); // 승인 대기 상태의 강좌만 반려할 수 있습니다.
        }

        course.setRejectionReason(reason);
        course.update(null, null, null, null, null, null, null, CourseStatus.REFUSE);
    }

    /**
     * 강좌 정보 수정
     *
     * @param courseId 강좌 ID
     * @param dto      수정할 데이터 (DTO)
     */
    @Transactional
    public void updateCourse(Long courseId, CourseUpdateReqDTO dto) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND)); // 존재하지 않는 강좌입니다.

        // 엔티티의 update 메서드를 호출하여 변경 감지(Dirty Checking)를 통해 DB 업데이트
        course.update(
                dto.getName(),
                dto.getCourseType(),
                dto.getMaxCapacity(),
                dto.getTuition(),
                dto.getSubjectId(),
                dto.getAcademicYearId(),
                dto.getTeacherDetailId() != null ? teacherDetailRepository.getReferenceById(dto.getTeacherDetailId())
                        : null,
                dto.getStatus());
    }

    /**
     * 강좌 변경 요청 생성 (즉시 반영 X)
     * 
     * @param courseId 강좌 ID
     * @param dto      변경할 데이터 DTO
     * @param reason   변경 사유
     */
    @Transactional
    public Long requestCourseUpdate(Long courseId, CourseUpdateReqDTO dto, String reason) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND)); // 존재하지 않는 강좌입니다.

        CourseChangeRequest request = CourseChangeRequest.builder()
                .course(course)
                .reason(reason)
                .targetMaxCapacity(dto.getMaxCapacity())
                .targetTuition(dto.getTuition())
                .targetTeacherDetailId(dto.getTeacherDetailId()) // 교사 변경 요청 추가
                .build();

        CourseChangeRequest savedRequest = courseChangeRequestRepository.save(request);
        return savedRequest.getId();
    }

    /**
     * 강좌 변경 요청 승인 (Course에 반영)
     */
    @Transactional
    public void approveChangeRequest(Long requestId) {
        CourseChangeRequest request = courseChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_CHANGE_REQUEST_NOT_FOUND)); // 존재하지 않는 요청입니다.

        if (request.getRequestStatus() != CourseChangeRequest.RequestStatus.PENDING) {
            throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH); // 대기 상태인 요청만 승인할 수 있습니다.
        }

        // 요청 데이터로 Course 업데이트 (핵심 데이터만 예시)
        Course course = request.getCourse();
        // DTO의 다른 필드들은 현재 ChangeRequest에 저장하지 않았으므로 null 처리하거나 기존 값 유지
        course.updateCourseInfo(
                null,
                null,
                request.getTargetMaxCapacity(),
                request.getTargetTuition(),
                null,
                null,
                request.getTargetTeacherDetailId() != null
                        ? teacherDetailRepository.getReferenceById(request.getTargetTeacherDetailId())
                        : null // 교사 변경 반영
        );

        request.approve();
    }

    /**
     * 강좌 변경 요청 반려
     */
    @Transactional
    public void rejectChangeRequest(Long requestId, String reason) {
        CourseChangeRequest request = courseChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_CHANGE_REQUEST_NOT_FOUND)); // 존재하지 않는 요청입니다.

        if (request.getRequestStatus() != CourseChangeRequest.RequestStatus.PENDING) {
            throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH); // 대기 상태인 요청만 반려할 수 있습니다.
        }

        request.reject(reason);
    }

    /**
     * [Helper] Strict CourseService-only fix workaround
     * Retrieves enrollments by filtering all enrollments in memory.
     * Note: In production, add findByCourseId to EnrollmentRepository index usage.
     */
    private List<Enrollment> getEnrollmentsByCourseId(Long courseId) {
        return enrollmentRepository.findAll().stream()
                .filter(e -> e.getCourseId().equals(courseId))
                .collect(Collectors.toList());
    }

    /**
     * 담당 교사 변경
     * - 변경하려는 교사의 스케줄 중복 확인 후 변경
     */
    @Transactional
    public void changeTeacher(Long courseId, Long newTeacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND)); // 존재하지 않는 강좌입니다.

        // 1. 유효성 검증: 새 담당 교사의 스케줄 중복 확인
        List<CourseTimeSlot> timeSlots = course.getTimeSlots();
        for (CourseTimeSlot slot : timeSlots) {
            int conflictCount = courseMapper.countTeacherSchedule(
                    course.getAcademicYearId(),
                    newTeacherId,
                    slot.getDayOfWeek(),
                    slot.getPeriod());

            if (conflictCount > 0) {
                throw new BusinessException(ErrorCode.INSTRUCTOR_TIMETABLE_CONFLICT); // 해당 교사는 해당 시간에 이미 수업이 있습니다.
            }
        }

        // 2. 교사 변경 반영
        course.updateCourseInfo(null, null, null, null, null, null,
                teacherDetailRepository.getReferenceById(newTeacherId));

        // 3. 알림 발송
        // Lazy Loading Issue 방지를 위해 Enrollment 조회 필요할 수 있음
        List<Enrollment> enrollments = getEnrollmentsByCourseId(courseId);
        notificationService.send(enrollments, "담당 선생님이 변경되었습니다.");
    }

    /**
     * 학생 수강 일괄/강제 등록
     * - studentIds: 학생 ID 리스트 (1명이면 개별 등록, 여러 명이면 일괄 등록)
     * - force: 정원 초과 무시 여부
     * - 중복 시간표 체크:
     * - 선택과목(ELECTIVE) 중복 시: 기존 내역 자동 취소 후 등록
     * - 필수과목(MANDATORY) 중복 시: 예외 발생 (등록 불가)
     */
    @Transactional
    public void enrollStudents(Long courseId, List<Long> studentIds, boolean force) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND)); // 존재하지 않는 강좌입니다.

        // 1. 정원 초과 체크 (force=true이면 무시)
        if (!force) {
            long currentCount = getEnrollmentsByCourseId(courseId).stream()
                    .filter(e -> e.getStatus() == EnrollmentStatus.APPLIED)
                    .count();
            if (currentCount + studentIds.size() > course.getMaxCapacity()) {
                throw new BusinessException(ErrorCode.COURSE_CAPACITY_FULL); // 수강 정원이 초과되었습니다.
            }
        }

        // 2. 학생 상세 정보 일괄 조회
        List<StudentDetail> students = studentDetailRepository.findAllById(studentIds);
        if (students.size() != studentIds.size()) {
            List<Long> foundIds = students.stream().map(StudentDetail::getId).toList();
            List<Long> missingIds = studentIds.stream()
                    .filter(id -> !foundIds.contains(id))
                    .collect(Collectors.toList());
            throw new BusinessException(ErrorCode.STUDENT_NOT_FOUND); // 존재하지 않는 학생 ID가 포함되어 있습니다.
        }

        // 학생 ID(User ID)로 빠르게 조회하기 위해 Map으로 변환
        Map<Long, StudentDetail> studentMap = students.stream()
                .collect(Collectors.toMap(
                        s -> s.getUser().getUserId(),
                        java.util.function.Function.identity()));

        // 3. 학생별 등록 프로세스
        for (Long studentId : studentIds) {
            StudentDetail student = studentMap.get(studentId);
            if (student == null)
                continue;

            // 3-1. 시간표 중복 검사
            for (CourseTimeSlot slot : course.getTimeSlots()) {
                List<Map<String, Object>> conflicts = courseMapper.findConflictingEnrollments(
                        course.getAcademicYearId(),
                        studentId,
                        slot.getDayOfWeek(),
                        slot.getPeriod());

                for (Map<String, Object> conflict : conflicts) {
                    String existingTypeStr = (String) conflict.get("courseType");
                    CourseType existingType = CourseType.valueOf(existingTypeStr);
                    Long existingEnrollmentId = (Long) conflict.get("enrollmentId");

                    if (existingType == CourseType.MANDATORY) {
                        throw new BusinessException(ErrorCode.STUDENT_REQUIRED_COURSE_CONFLICT); // 학생은 해당 시간에 이미 필수 과목이
                                                                                                 // 있어 등록할 수 없습니다.
                    } else {
                        // 선택 과목이면 자동 취소
                        Enrollment existingEnrollment = enrollmentRepository
                                .findById(existingEnrollmentId).orElseThrow();
                        existingEnrollment.cancel();
                    }
                }
            }

            // 3-2. 수강 등록 (필드명 studentDetail로 수정)
            Enrollment enrollment = Enrollment.builder()
                    .studentDetail(student)
                    .courseId(course.getCourseId())
                    .build();
            enrollmentRepository.save(enrollment);
        }
    }

    /**
     * 강좌 폐강 (상태 변경 및 수강생 일괄 취소) -> 강좌 삭제(DeleteCourse) (Soft Delete)
     * - 강좌 상태: CANCELED
     * - 수강생 상태: FORCED_CANCELED
     */
    @Transactional
    public void deleteCourse(Long courseId, String reason) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND)); // 존재하지 않는 강좌입니다.

        // 1. 강좌 상태 변경 (CANCELED)
        course.changeStatus(CourseStatus.CANCELED);

        // 2. 수강생 일괄 취소 (FORCED_CANCELED) & 환불 처리
        List<Enrollment> enrollments = getEnrollmentsByCourseId(courseId);
        for (Enrollment enrollment : enrollments) {
            if (enrollment.getStatus() == EnrollmentStatus.APPLIED) {
                // [변경] cancel() -> forceCancel(reason) 호출하여 사유 저장
                enrollment.forceCancel("강좌 폐강: " + reason);
                // 환불 로직 호출
                refundService.processRefund(enrollment.getStudentDetail().getUser().getUserId(), courseId,
                        "강좌 폐강: " + reason);
            }
        }

        // 3. 폐강 알림 전송
        notificationService.send(enrollments, "강좌(" + course.getName() + ")가 폐강되었습니다. 사유: " + reason);
    }

    /**
     * 학생 수강 강제 취소 (개별)
     */
    @Transactional
    public void forceCancelStudent(Long courseId, Long studentId, String reason) {
        if (reason == null || reason.trim().isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_INPUT_VALUE); // 강제 취소 사유는 필수입니다.
        }

        Enrollment enrollment = getEnrollmentsByCourseId(courseId)
                .stream()
                .filter(e -> e.getStudentDetail().getUser().getUserId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_ENROLLMENT_NOT_FOUND)); // 해당 강좌의 수강생 정보를 찾을 수
                                                                                                  // 없습니다.

        // [변경] forceCancel 호출
        enrollment.forceCancel(reason);
    }

    @Transactional(readOnly = true)
    public StudentDetailResDTO getStudentDetail(Long courseId, Long studentId) {
        Enrollment enrollment = getEnrollmentsByCourseId(courseId)
                .stream()
                .filter(e -> e.getStudentDetail().getUser().getUserId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_ENROLLMENT_NOT_FOUND)); // 해당 강좌의 수강생 정보를 찾을 수
                                                                                                  // 없습니다.

        long presentCount = 0; // TODO: 실제 출결 카운트 로직 적용 필요
        long lateCount = 0;
        long absentCount = 0;

        return StudentDetailResDTO.builder()
                .studentId(studentId)
                .studentName(enrollment.getStudentDetail().getUser().getName())
                // [변경] 실제 메모 조회
                .memo(enrollment.getMemo())
                .attendancePresent(presentCount)
                .attendanceLate(lateCount)
                .attendanceAbsent(absentCount)
                .assignmentTotal(0)
                .assignmentSubmitted(0)
                .build();
    }

    @Transactional
    public void updateStudentMemo(Long courseId, Long studentId, String memo) {
        Enrollment enrollment = getEnrollmentsByCourseId(courseId)
                .stream()
                .filter(e -> e.getStudentDetail().getUser().getUserId().equals(studentId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_ENROLLMENT_NOT_FOUND)); // 해당 강좌의 수강생 정보를 찾을 수
                                                                                                  // 없습니다.

        enrollment.updateMemo(memo);
    }

    /**
     * 교사별 강좌 목록 조회 (Paging)
     */
    @Transactional(readOnly = true)
    public Page<CourseListResDTO> getCourseList(Long teacherDetailId, Pageable pageable) {
        return courseRepository.findByTeacherDetail_Id(teacherDetailId, pageable)
                .map(this::convertToCourseListResDTO);
    }

    /**
     * 전체 강좌 목록 조회 (관리자용 - Paging)
     */
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
                .teacherName("Teacher_" + course.getTeacherDetailId()) // Placeholder: 실제 교사명 조회 필요
                .build();
    }

    /**
     * 강좌 상태 수동 변경 (조기 마감 / 재오픈)
     * - OPEN <-> CLOSED 상태 전환만 허용 (기타 상태 변경은 별도 프로세스 따름)
     */
    @Transactional
    public void changeCourseStatus(Long courseId, CourseStatus status) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COURSE_NOT_FOUND)); // 존재하지 않는 강좌입니다.

        // 유효성 검증: 수동 변경은 OPEN, CLOSED 만 가능하도록 제한
        if (status != CourseStatus.OPEN && status != CourseStatus.CLOSED) {
            throw new BusinessException(ErrorCode.COURSE_CONDITION_MISMATCH); // 수동 상태 변경은 개설(OPEN) 또는 마감(CLOSED) 상태로만
                                                                              // 가능합니다.
        }

        course.changeStatus(status);
    }

    /**
     * 교사 주간 시간표 조회
     * Grid 형태이므로, 요일/교시별로 매핑된 리스트 반환
     */
    @Transactional(readOnly = true)
    public com.mycompany.project.course.dto.TeacherTimetableResDTO getTeacherTimetable(Long teacherDetailId,
            Long academicYearId) {
        List<Map<String, Object>> timeSlotsMap = courseMapper.findTeacherTimetable(academicYearId,
                teacherDetailId);

        List<com.mycompany.project.course.dto.TeacherTimetableResDTO.TimeSlotInfo> timeSlots = timeSlotsMap.stream()
                .map(m -> com.mycompany.project.course.dto.TeacherTimetableResDTO.TimeSlotInfo.builder()
                        .dayOfWeek((String) m.get("dayOfWeek"))
                        .period((Integer) m.get("period"))
                        .courseId((Long) m.get("courseId"))
                        .courseName((String) m.get("courseName"))
                        .classroom((String) m.get("classroom"))
                        .courseType(String.valueOf(m.get("courseType")))
                        .build())
                .collect(Collectors.toList());

        return com.mycompany.project.course.dto.TeacherTimetableResDTO.builder()
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
        response.setTargetGrade(course.getAcademicYearId().intValue()); // 임시: academicYearId를 학년으로 사용

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
