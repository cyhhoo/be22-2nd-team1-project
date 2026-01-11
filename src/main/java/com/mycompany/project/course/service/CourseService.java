package com.mycompany.project.course.service;

import com.mycompany.project.course.dto.TimeSlotDTO;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.course.mapper.CourseMapper;

import org.springframework.transaction.annotation.Transactional;
import com.mycompany.project.course.dto.CourseUpdateReqDTO;
import com.mycompany.project.course.entity.Course;

import com.mycompany.project.course.dto.CourseCreateReqDTO;
import com.mycompany.project.course.entity.CourseStatus;
import com.mycompany.project.course.entity.CourseTimeSlot;
import com.mycompany.project.course.repository.CourseChangeRequestRepository;
import com.mycompany.project.course.entity.CourseChangeRequest;
import java.util.List;

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
    private final com.mycompany.project.enrollment.repository.EnrollmentRepository enrollmentRepository;
    private final CourseMapper courseMapper;

    /**
     * 강좌 개설 신청 (상태: PENDING)
     */
    @Transactional
    public void createCourse(CourseCreateReqDTO dto) {
        // 1. 유효성 검증 (DTO Validation으로 처리되지만, 비즈니스 로직 상 추가 검증 필요 시 여기에 작성)
        // (기존의 if (maxCapacity <= 0) 등은 @Min 어노테이션으로 대체됨)

        // 2. 강좌 엔티티 생성 (Status = PENDING)
        Course course = Course.builder()
                .name(dto.getName())
                .courseType(dto.getCourseType())
                .maxCapacity(dto.getMaxCapacity())
                .tuition(dto.getTuition())
                .subjectId(dto.getSubjectId())
                .academicYearId(dto.getAcademicYearId())
                .teacherDetailId(dto.getTeacherDetailId())
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
                    throw new IllegalStateException("해당 시간에 담당 교사의 다른 수업이 존재합니다.");
                }

                // 강의실 중복 검증 (MyBatis)
                int classroomConflictCount = courseMapper.countClassroomSchedule(
                        dto.getAcademicYearId(),
                        slotDto.getClassroom(),
                        slotDto.getDayOfWeek(),
                        slotDto.getPeriod());

                if (classroomConflictCount > 0) {
                    throw new IllegalStateException("해당 시간에 강의실이 이미 사용 중입니다.");
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
        // Cascade.ALL로 인해 timeSlots도 자동 저장됨
    }

    /**
     * 강좌 승인 (PENDING -> OPEN)
     */
    @Transactional
    public void approveCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강좌입니다."));

        if (course.getStatus() != CourseStatus.PENDING) {
            throw new IllegalStateException("승인 대기 상태의 강좌만 승인할 수 있습니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강좌입니다."));

        if (course.getStatus() != CourseStatus.PENDING) {
            throw new IllegalStateException("승인 대기 상태의 강좌만 반려할 수 있습니다.");
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강좌입니다. ID: " + courseId));

        // 엔티티의 update 메서드를 호출하여 변경 감지(Dirty Checking)를 통해 DB 업데이트
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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강좌입니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

        if (request.getRequestStatus() != CourseChangeRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("대기 상태인 요청만 승인할 수 있습니다.");
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
                request.getTargetTeacherDetailId() // 교사 변경 반영
        );

        request.approve();
    }

    /**
     * 강좌 변경 요청 반려
     */
    @Transactional
    public void rejectChangeRequest(Long requestId, String reason) {
        CourseChangeRequest request = courseChangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

        if (request.getRequestStatus() != CourseChangeRequest.RequestStatus.PENDING) {
            throw new IllegalStateException("대기 상태인 요청만 반려할 수 있습니다.");
        }

        request.reject(reason);
    }

    /**
     * 담당 교사 변경
     * - 변경하려는 교사의 스케줄 중복 확인 후 변경
     */
    @Transactional
    public void changeTeacher(Long courseId, Long newTeacherId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강좌입니다."));

        // 1. 유효성 검증: 새 담당 교사의 스케줄 중복 확인
        List<CourseTimeSlot> timeSlots = course.getTimeSlots();
        for (CourseTimeSlot slot : timeSlots) {
            int conflictCount = courseMapper.countTeacherSchedule(
                    course.getAcademicYearId(),
                    newTeacherId,
                    slot.getDayOfWeek(),
                    slot.getPeriod());

            if (conflictCount > 0) {
                throw new IllegalStateException(
                        String.format("해당 교사는 %s %d교시에 이미 수업이 있습니다.", slot.getDayOfWeek(), slot.getPeriod()));
            }
        }

        // 2. 교사 변경 반영
        course.updateCourseInfo(null, null, null, null, null, null, newTeacherId);

        // 3. 알림 발송 (Simulated)
        // notificationService.send(course.getEnrollments(), "담당 선생님이 변경되었습니다.");
    }

}
