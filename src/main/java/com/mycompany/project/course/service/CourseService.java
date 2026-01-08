package com.mycompany.project.course.service;

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

@Service
@RequiredArgsConstructor
public class CourseService {
    private final CourseRepository courseRepository;
    private final CourseMapper courseMapper;

    /**
     * 강좌 개설 신청 (상태: PENDING)
     */
    @Transactional
    public void createCourse(CourseCreateReqDTO dto) {
        // 1. 유효성 검증 (최대 정원, 필수 값 등) -> DTO validation or here
        if (dto.getMaxCapacity() == null || dto.getMaxCapacity() <= 0) {
            throw new IllegalArgumentException("최대 정원은 0보다 커야 합니다.");
        }

        // 2. 강좌 엔티티 생성 (Status = PENDING)
        Course course = Course.builder()
                .name(dto.getName())
                .courseType(dto.getCourseType())
                .maxCapacity(dto.getMaxCapacity())
                .tuition(dto.getTuition())
                .subjectId(dto.getSubjectId())
                .academicYearId(dto.getAcademicYearId())
                .teacherDetailId(dto.getTeacherDetailId())
                .build();
        // save를 먼저 해서 ID를 확보하거나, Cascade 설정을 이용
        // 여기서는 builder 패턴으로 만들고, 엔티티 내부에서 초기값을 처리했음(Status 등).
        // 하지만 builder에 status 설정이 없으므로 엔티티 기본값(OPEN)을 덮어써야 할 수도 있음.
        // Course 엔티티에서 초기값을 필드 선언 시 PENDING으로 변경하거나, builder 후 설정 필요.
        // 현재 Course 엔티티의 초기값은 OPEN임.
        // 따라서 명시적으로 PENDING 설정 필요.

        // *참고: Course 엔티티에 public setStatus가 없으므로 update 메서드 사용하거나 Status 변경 메서드 추가 필요.
        // 혹은 Builder에 status 포함. (Builder에 status 필드가 없음, Course 생성자 확인 필요)

        // Course 엔티티 Builder 확인 결과: status 필드 없음.
        // -> Course를 먼저 생성 후, 별도의 메서드를 통해 Status를 변경하거나,
        // Builder에 status를 추가하는 것이 좋음.
        // 여기서는 엔티티 수정 없이 진행하기 위해, 일단 save 후 update 메서드를 통해 상태 변경 (약간 비효율적이나 안전)
        // 또는 Course 엔티티에 편의 메서드 추가 권장.
        // **계획 변경 없이 진행하려면**: Course 엔티티는 protected 생성자 + Builder 패턴임.
        // Builder에 없는 필드는 기본값 적용됨.

        // [수정] Course.java의 Builder에 status 추가가 안되어 있으므로
        // Course 생성 후 바로 접근하여 수정할 수 있는 방법이 제한적(update 메서드 사용).
        // 하지만 update 메서드는 "수정"용이므로, 최초 생성 시점의 상태 제어를 위해
        // Course 엔티티 생성을 위한 정적 팩토리 메서드나 Builder 수정이 이상적임.
        // **여기서는 update 메서드를 활용하여 status를 PENDING으로 설정합니다.**

        // Course 저장
        course = courseRepository.save(course);
        course.update(null, null, null, null, null, null, null, CourseStatus.PENDING); // 상태 변경

        // 3. 시간표(TimeSlot) 저장 및 중복 검증
        if (dto.getTimeSlots() != null) {
            for (CourseCreateReqDTO.TimeSlotDTO slotDto : dto.getTimeSlots()) {
                // 교사 중복 검증
                boolean isTeacherConflict = courseRepository.existsByTeacherAndSchedule(
                        dto.getAcademicYearId(),
                        dto.getTeacherDetailId(),
                        slotDto.getDayOfWeek(),
                        slotDto.getPeriod());
                if (isTeacherConflict) {
                    throw new IllegalStateException("해당 시간에 담당 교사의 다른 수업이 존재합니다.");
                }

                // 강의실 중복 검증
                boolean isClassroomConflict = courseRepository.existsByClassroomAndSchedule(
                        dto.getAcademicYearId(),
                        slotDto.getClassroom(),
                        slotDto.getDayOfWeek(),
                        slotDto.getPeriod());
                if (isClassroomConflict) {
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
        // Cascade.ALL로 인해 timeSlots도 자동 저장됨 (save를 다시 호출하지 않아도 Dirty Checking으로 저장되지
        // 않음,
        // 연관관계 맺은 후 save 필요하거나, 처음에 course를 save하지 않고 연관관계 맺은 뒤 save해야 함)

        // [수정 로직]:
        // 1. Course 객체 생성 (비영속) -> Status PENDING 설정 불가(Builder없음).
        // 2. 일단 Course 저장 (영속) -> OPEN 상태
        // 3. update 메서드로 PENDING 변경
        // 4. TimeSlot 검증 및 추가
        // 5. Transaction 종료 시 Dirty Checking으로 TimeSlot Insert 및 Course Update 발생

        // 주의: CreatedDate 등 필수 컬럼 문제가 없다면 동작함.
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
     */
    @Transactional
    public void refuseCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 강좌입니다."));

        if (course.getStatus() != CourseStatus.PENDING) {
            throw new IllegalStateException("승인 대기 상태의 강좌만 반려할 수 있습니다.");
        }

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
}
