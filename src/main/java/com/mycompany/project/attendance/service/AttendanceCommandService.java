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
import com.mycompany.project.enrollment.entity.EnrollmentStatus;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.user.command.domain.aggregate.TeacherDetail;
import com.mycompany.project.user.command.domain.repository.StudentDetailRepository;
import com.mycompany.project.user.command.domain.repository.TeacherDetailRepository;
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

    // 수강신청이 정상(APPLIED)인 학생만 출결 대상으로 잡는다.
    private static final EnrollmentStatus ENROLLMENT_STATUS_APPLIED = EnrollmentStatus.APPLIED;

    // 출석부 자동 생성 시 기본으로 깔아줄 출결 코드 (보통 PRESENT)
    private static final String DEFAULT_ATTENDANCE_CODE = "PRESENT";

    // 출결 데이터 쓰기(JPA)
    private final AttendanceRepository attendanceRepository;

    // 출결 코드 조회(JPA) - 기본코드(PRESENT) 찾거나, 활성코드인지 검증할 때 사용
    private final AttendanceCodeRepository attendanceCodeRepository;

    // 수강신청 조회(JPA) - 강좌에 등록된 학생 목록 뽑는 용도
    private final EnrollmentRepository enrollmentRepository;

    // 강좌 조회(JPA) - 담당교사/강좌 존재 여부 확인
    private final CourseRepository courseRepository;

    // 조회는 MyBatis로 별도 서비스(읽기 모델)
    private final AttendanceQueryService attendanceQueryService;

    // 담임 여부(학년/반) 확인용
    private final TeacherDetailRepository teacherDetailRepository;

    // 학생의 학년/반 확인용
    private final StudentDetailRepository studentDetailRepository;

    /**
     * 출석부 자동 생성(없으면 생성 + 있으면 그대로 조회)
     * - 과목 담당 교사가 특정 날짜/교시에 대해 출석부를 "처음 열 때" 많이 쓰는 로직
     * - enrollment 기준으로 학생별 출결 레코드가 없으면 생성한다.
     */
    @Transactional
    public List<AttendanceListResponse> generateAttendances(AttendanceCreateRequest request) {

        // 필수값 검증
        validateGenerateRequest(request);

        // 강좌 존재 확인
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과목입니다."));

        // 권한 체크: 과목 담당 교사만 생성 가능
        ensureCourseTeacher(request.getUserId(), course);

        // 기본 출결 코드(PRESENT) 조회 (활성화된 코드만)
        AttendanceCode defaultCode = attendanceCodeRepository.findByCodeAndActiveTrue(DEFAULT_ATTENDANCE_CODE)
                .orElseThrow(() -> new IllegalArgumentException("기본 출결 코드(PRESENT)를 찾을 수 없습니다."));

        // 해당 강좌에 신청된 학생 목록(APPLIED) 조회
        List<Enrollment> enrollments = enrollmentRepository.findByCourse_CourseIdAndStatus(
                request.getCourseId(), ENROLLMENT_STATUS_APPLIED);
        if (enrollments.isEmpty()) {
            // 수강신청이 없으면 생성할 출결도 없음
            return List.of();
        }

        // 이미 생성된 출결이 있으면 건너뛰고, 없는 것만 생성한다.
        List<Attendance> toSave = new ArrayList<>();
        byte period = request.getPeriod().byteValue();

        for (Enrollment enrollment : enrollments) {

            // (enrollmentId + 날짜 + 교시) 기준으로 출결이 이미 있는지 확인
            Optional<Attendance> existing = attendanceRepository.findByEnrollmentIdAndClassDateAndPeriod(
                    enrollment.getEnrollmentId(), request.getClassDate(), period);

            if (existing.isEmpty()) {
                // 없으면 기본 코드(PRESENT)로 새 출결 생성
                Attendance attendance = new Attendance(
                        request.getClassDate(),
                        period,
                        defaultCode.getId(),
                        enrollment.getEnrollmentId(),
                        request.getUserId(),
                        null // 처음 생성 시 reason은 보통 비움
                );
                toSave.add(attendance);
            }
        }

        // 모은 것들만 한번에 저장
        if (!toSave.isEmpty()) {
            attendanceRepository.saveAll(toSave);
        }

        // 생성 후에는 "조회 API"와 동일한 결과 형태로 반환한다(프론트가 편함)
        AttendanceSearchRequest searchRequest = AttendanceSearchRequest.builder()
                .courseId(request.getCourseId())
                .fromDate(request.getClassDate())
                .toDate(request.getClassDate())
                .period(request.getPeriod())
                .build();

        return attendanceQueryService.search(searchRequest);
    }

    /**
     * 출결 저장(등록/수정)
     * - 담당 교사가 출결 코드/사유를 저장한다.
     * - 확정/마감 상태는 수정 불가.
     */
    @Transactional
    public void saveAttendances(AttendanceUpdateRequest request) {

        // 필수값 + items 검증
        validateSaveRequest(request);

        // 강좌 존재 확인
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과목입니다."));

        // 권한 체크: 과목 담당 교사만 저장 가능
        ensureCourseTeacher(request.getUserId(), course);

        // 과목에 등록된 수강신청 목록을 가져와서 "요청 enrollmentId가 이 강좌 소속인지" 검증한다.
        List<Enrollment> enrollments = enrollmentRepository.findByCourse_CourseIdAndStatus(
                request.getCourseId(), ENROLLMENT_STATUS_APPLIED);

        List<Long> enrollmentIds = enrollments.stream()
                .map(Enrollment::getEnrollmentId)
                .collect(Collectors.toList());

        byte period = request.getPeriod().byteValue();

        // 저장할 출결 엔티티들을 모아서 saveAll로 처리
        List<Attendance> toSave = new ArrayList<>();

        for (AttendanceUpdateItemRequest item : request.getItems()) {

            // 항목 단위 필수값 검증
            if (item.getEnrollmentId() == null || item.getAttendanceCodeId() == null) {
                throw new IllegalArgumentException("출결 항목 정보가 누락되었습니다.");
            }

            // 요청 enrollmentId가 이 강좌에 속한 학생인지 검증
            if (!enrollmentIds.contains(item.getEnrollmentId())) {
                throw new IllegalArgumentException("해당 과목의 수강 신청이 아닙니다.");
            }

            // 출결 코드 존재/활성 여부 검증
            AttendanceCode code = attendanceCodeRepository.findById(item.getAttendanceCodeId())
                    .orElseThrow(() -> new IllegalArgumentException("출결 코드가 존재하지 않습니다."));

            if (!code.isActive()) {
                throw new IllegalStateException("비활성화된 출결 코드는 사용할 수 없습니다.");
            }

            // 기존 출결이 있는지 조회
            Optional<Attendance> existing = attendanceRepository.findByEnrollmentIdAndClassDateAndPeriod(
                    item.getEnrollmentId(), request.getClassDate(), period);

            if (existing.isPresent()) {
                // 있으면 수정 처리
                Attendance attendance = existing.get();

                // 확정/마감이면 저장 불가
                if (attendance.getState() == AttendanceState.CONFIRMED
                        || attendance.getState() == AttendanceState.CLOSED) {
                    throw new IllegalStateException("확정/마감 상태의 출결은 수정할 수 없습니다.");
                }

                // 엔티티 비즈니스 메서드로 저장 처리(코드/사유/저장자/시간 갱신)
                attendance.saveByTeacher(request.getUserId(), code.getId(), item.getReason());
                toSave.add(attendance);

            } else {
                // 없으면 신규 출결 생성(저장 상태=SAVED로 생성됨)
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

        // 모은 엔티티들을 한번에 저장
        if (!toSave.isEmpty()) {
            attendanceRepository.saveAll(toSave);
        }
    }

    /**
     * 출결 확정(CONFIRMED)
     * - 담당교사가 아닌 경우라도 "담임"이면 확정 가능
     * - 해당 날짜/교시에 미입력 출결이 있으면 확정 불가
     */
    @Transactional
    public void confirmAttendances(AttendanceConfirmRequest request) {

        // 필수값 검증
        validateConfirmRequest(request);

        // 강좌 존재 확인
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 과목입니다."));

        // 권한 체크:
        // 1) 과목 담당교사면 OK
        // 2) 아니면 담임(학년/반 일치)인지 확인
        ensureConfirmAuthority(request.getUserId(), course);

        // 강좌에 속한 학생 목록(APPLIED) 조회
        List<Enrollment> enrollments = enrollmentRepository.findByCourse_CourseIdAndStatus(
                request.getCourseId(), ENROLLMENT_STATUS_APPLIED);

        if (enrollments.isEmpty()) {
            throw new IllegalStateException("확정할 출결 대상이 없습니다.");
        }

        // enrollmentId 목록 추출
        List<Long> enrollmentIds = enrollments.stream()
                .map(Enrollment::getEnrollmentId)
                .collect(Collectors.toList());

        // 미입력 출결 체크:
        // enrollment 수만큼 출결 레코드가 있어야 확정 가능
        long attendanceCount = attendanceRepository.countByEnrollmentIdInAndClassDateAndPeriod(
                enrollmentIds, request.getClassDate(), request.getPeriod().byteValue());

        if (attendanceCount < enrollmentIds.size()) {
            throw new IllegalStateException("미입력 출결이 있어 확정할 수 없습니다.");
        }

        // 대상 출결들을 한번에 조회
        List<Attendance> attendances = attendanceRepository.findByEnrollmentIdInAndClassDateAndPeriod(
                enrollmentIds, request.getClassDate(), request.getPeriod().byteValue());

        // 하나씩 상태 확인 후 확정 처리
        for (Attendance attendance : attendances) {

            // 마감(CLOSED)은 확정 자체가 불가
            if (attendance.getState() == AttendanceState.CLOSED) {
                throw new IllegalStateException("마감된 출결은 확정할 수 없습니다.");
            }

            // 이미 확정이면 넘어감
            if (attendance.getState() == AttendanceState.CONFIRMED) {
                continue;
            }

            // 엔티티 비즈니스 메서드로 확정 처리
            attendance.confirm(request.getUserId());
        }

        // 변경된 출결들을 한번에 저장
        attendanceRepository.saveAll(attendances);
    }

    // ====== 요청 DTO 검증 메서드들 ======

    // 출석부 생성 요청 필수값 검증
    private void validateGenerateRequest(AttendanceCreateRequest request) {
        if (request == null
                || request.getCourseId() == null
                || request.getClassDate() == null
                || request.getPeriod() == null
                || request.getUserId() == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }
    }

    // 출결 저장 요청 필수값 + items 검증
    private void validateSaveRequest(AttendanceUpdateRequest request) {
        if (request == null
                || request.getCourseId() == null
                || request.getClassDate() == null
                || request.getPeriod() == null
                || request.getUserId() == null
                || request.getItems() == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }
        if (request.getItems().isEmpty()) {
            throw new IllegalArgumentException("저장할 출결 항목이 없습니다.");
        }
    }

    // 출결 확정 요청 필수값 검증
    private void validateConfirmRequest(AttendanceConfirmRequest request) {
        if (request == null
                || request.getCourseId() == null
                || request.getClassDate() == null
                || request.getPeriod() == null
                || request.getUserId() == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }
    }

    // ====== 권한 검증 메서드들 ======

    /**
     * 과목 담당 교사 권한 체크
     * - 강좌에 저장된 담당교사(teacherDetailId)가 요청 userId와 같아야 한다.
     */
    private void ensureCourseTeacher(Long userId, Course course) {
        if (!Objects.equals(course.getTeacherDetail().getId(), userId)) {
            throw new IllegalStateException("과목 담당 교사만 처리할 수 있습니다.");
        }
    }

    /**
     * 확정 권한 체크
     * - 담당교사면 바로 통과
     * - 아니면 담임인지 확인(학년/반 일치)
     */
    private void ensureConfirmAuthority(Long userId, Course course) {

        // 담당교사면 확정 가능
        if (Objects.equals(course.getTeacherDetail().getId(), userId)) {
            return;
        }

        // 담당교사가 아니라면 담임 권한(학년/반)이 있어야 함
        TeacherDetail teacherDetail = teacherDetailRepository.findById(userId)
                .orElseThrow(() -> new IllegalStateException("담임 권한이 없습니다."));

        if (teacherDetail.getHomeroomGrade() == null || teacherDetail.getHomeroomClassNo() == null) {
            throw new IllegalStateException("담임 권한이 없습니다.");
        }

        // 강좌에 속한 학생 목록 조회
        List<Enrollment> enrollments = enrollmentRepository.findByCourse_CourseIdAndStatus(
                course.getCourseId(), ENROLLMENT_STATUS_APPLIED);

        if (enrollments.isEmpty()) {
            throw new IllegalStateException("확정할 출결 대상이 없습니다.");
        }

        // 학생ID만 추출
        List<Long> studentIds = enrollments.stream()
                .map(enrollment -> enrollment.getStudentDetailId().getId())
                .collect(Collectors.toList());

        // 담임의 학년/반과 학생들이 완전히 일치하는지 확인
        String classNo = String.valueOf(teacherDetail.getHomeroomClassNo());

        long matchedCount = studentDetailRepository.countByIdInAndGradeAndClassNo(
                studentIds, teacherDetail.getHomeroomGrade(), classNo);

        // 한 명이라도 학년/반이 다르면 "그 반 담임"이 아니라고 판단
        if (matchedCount != studentIds.size()) {
            throw new IllegalStateException("담임 권한이 없습니다.");
        }
    }
}
