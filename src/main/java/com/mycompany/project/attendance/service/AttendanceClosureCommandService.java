package com.mycompany.project.attendance.service;

import com.mycompany.project.attendance.dto.request.AttendanceClosureRequest;
import com.mycompany.project.attendance.entity.Attendance;
import com.mycompany.project.attendance.entity.AttendanceClosure;
import com.mycompany.project.attendance.entity.enums.AttendanceState;
import com.mycompany.project.attendance.entity.enums.ScopeType;
import com.mycompany.project.attendance.repository.AttendanceClosureRepository;
import com.mycompany.project.attendance.repository.AttendanceRepository;
import com.mycompany.project.course.entity.Course;
import com.mycompany.project.course.repository.CourseRepository;
import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.enrollment.entity.EnrollmentStatus;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.schedule.command.domain.aggregate.AcademicYear;
import com.mycompany.project.schedule.command.domain.repository.AcademicYearRepository;
import com.mycompany.project.user.command.domain.aggregate.Role;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.UserRepository;
import com.mycompany.project.user.repository.StudentDetailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AttendanceClosureCommandService {

    // "수강신청이 정상(APPLIED)인 학생"만 출결 마감 대상으로 잡기 위한 상수
    private static final EnrollmentStatus ENROLLMENT_STATUS_APPLIED = EnrollmentStatus.APPLIED;

    // 출결(Attendance) 데이터 접근(JPA) - 마감 처리 시 상태를 CLOSED로 바꿔야 함
    private final AttendanceRepository attendanceRepository;

    // 마감 이력(AttendanceClosure) 저장(JPA)
    private final AttendanceClosureRepository attendanceClosureRepository;

    // 학년도/학기 범위(SEMESTER) 마감할 때 기간(start~end) 가져오기
    private final AcademicYearRepository academicYearRepository;

    // 특정 학년도에 속한 강좌 목록을 가져오기 위해 사용
    private final CourseRepository courseRepository;

    // 강좌에 등록된 학생(수강신청) 목록을 가져오기 위해 사용
    private final EnrollmentRepository enrollmentRepository;

    // 학년/반 필터가 있을 때 학생 상세에서 필터링하기 위해 사용
    private final StudentDetailRepository studentDetailRepository;

    // 요청자가 관리자(Role.ADMIN)인지 확인하기 위해 사용
    private final UserRepository userRepository;

    /**
     * 출결 마감 처리
     * - 관리자가 특정 범위(월/학기)에 대해 출결을 CLOSED로 전환하고, 마감 이력을 남긴다.
     */
    @Transactional // 출결 상태 변경 + 마감이력 저장을 하나의 트랜잭션으로 묶음
    public void closeAttendances(AttendanceClosureRequest request) {

        // 요청 파라미터 기본 검증(필수값 누락 방지)
        validateRequest(request);

        // 권한 체크: 관리자만 마감 가능
        ensureAdmin(request.getUserId());

        // 1) 마감 범위에 맞는 날짜 구간 계산
        // - MONTH면 scopeValue(yyyy-MM) 기반으로 해당 월 1일~말일
        // - SEMESTER면 academicYearId로 학기 start~end
        LocalDate fromDate = resolveFromDate(request);
        LocalDate toDate = resolveToDate(request, fromDate);

        // 2) 마감 대상 강좌 목록 결정
        // - request.courseId가 있으면 그 강좌만
        // - 없으면 academicYearId에 속한 강좌 전체
        List<Long> courseIds = resolveCourseIds(request);
        if (courseIds.isEmpty()) {
            // 대상 강좌가 없으면 마감할 게 없음(그냥 종료)
            return;
        }

        // 3) 강좌 범위 → 수강신청(APPLIED) 학생들만 가져와서 대상 좁히기
        List<Enrollment> enrollments =
                enrollmentRepository.findByCourseIdInAndStatus(courseIds, ENROLLMENT_STATUS_APPLIED);

        if (enrollments.isEmpty()) {
            // 수강신청이 없으면 마감할 출결도 없을 가능성이 큼
            return;
        }

        // 4) 학년/반 조건이 있으면 student_detail 기준으로 enrollmentId를 더 줄인다.
        List<Long> enrollmentIds = filterEnrollmentIdsByClass(request, enrollments);
        if (enrollmentIds.isEmpty()) {
            return;
        }

        // 5) 최종 대상 enrollmentId + 기간(from~to)에 해당하는 출결을 전부 조회한다.
        List<Attendance> allAttendances =
                attendanceRepository.findByEnrollmentIdInAndClassDateBetween(enrollmentIds, fromDate, toDate);

        if (allAttendances.isEmpty()) {
            // 해당 기간에 출결 데이터가 없으면 마감할 게 없음
            return;
        }

        // 6) 마감 조건 검증:
        // - 확정(CONFIRMED) 또는 이미 마감(CLOSED)된 것만 마감 가능
        // - SAVED 같은 상태가 끼어 있으면 마감 차단
        boolean hasUnconfirmed = allAttendances.stream()
                .anyMatch(attendance ->
                        attendance.getState() != AttendanceState.CONFIRMED
                                && attendance.getState() != AttendanceState.CLOSED
                );

        if (hasUnconfirmed) {
            throw new IllegalStateException("확정되지 않은 출결이 있어 마감할 수 없습니다.");
        }

        // 7) CONFIRMED인 출결만 CLOSED로 상태 전환
        // (이미 CLOSED인 건은 건드릴 필요 없음)
        List<Attendance> attendances = allAttendances.stream()
                .filter(attendance -> attendance.getState() == AttendanceState.CONFIRMED)
                .collect(Collectors.toList());

        if (!attendances.isEmpty()) {
            for (Attendance attendance : attendances) {
                // 엔티티 내부 메서드로 상태 변경(Setter 대신)
                attendance.close();
            }
            // 변경된 출결들을 일괄 저장
            attendanceRepository.saveAll(attendances);
        }

        // 8) 마감 이력 저장(누가/어떤 범위를 마감했는지 남김)
        AttendanceClosure closure = new AttendanceClosure(
                request.getAcademicYearId(),
                request.getScopeType(),
                request.getScopeValue(),
                request.getGrade(),
                request.getClassNo(),
                request.getCourseId(),
                request.getUserId()
        );
        attendanceClosureRepository.save(closure);
    }

    /**
     * 마감 요청 필수값 검증
     * - academicYearId, scopeType, scopeValue, userId는 꼭 있어야 함
     */
    private void validateRequest(AttendanceClosureRequest request) {
        if (request == null
                || request.getAcademicYearId() == null
                || request.getScopeType() == null
                || request.getScopeValue() == null
                || request.getUserId() == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }
    }

    /**
     * 관리자 권한 체크
     * - user.role == ADMIN 인지 확인
     */
    private void ensureAdmin(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));

        if (user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("관리자만 마감 처리할 수 있습니다.");
        }
    }

    /**
     * 마감 시작일 계산
     * - MONTH: scopeValue(yyyy-MM)에서 1일로 계산
     * - SEMESTER: academicYear.startDate 사용
     */
    private LocalDate resolveFromDate(AttendanceClosureRequest request) {
        if (request.getScopeType() == ScopeType.MONTH) {
            // "2026-01" 같은 문자열을 YearMonth로 파싱
            YearMonth yearMonth = YearMonth.parse(request.getScopeValue());
            return yearMonth.atDay(1);
        }

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new IllegalArgumentException("학년도/학기 정보가 없습니다."));

        return academicYear.getStartDate();
    }

    /**
     * 마감 종료일 계산
     * - MONTH: 해당 월 마지막 일자로 계산
     * - SEMESTER: academicYear.endDate 사용
     */
    private LocalDate resolveToDate(AttendanceClosureRequest request, LocalDate fromDate) {
        if (request.getScopeType() == ScopeType.MONTH) {
            YearMonth yearMonth = YearMonth.parse(request.getScopeValue());
            return yearMonth.atEndOfMonth();
        }

        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
                .orElseThrow(() -> new IllegalArgumentException("학년도/학기 정보가 없습니다."));

        return academicYear.getEndDate();
    }

    /**
     * 마감 대상 강좌 목록 결정
     * - courseId가 있으면 단일 강좌만 마감
     * - 없으면 academicYearId에 속한 강좌 전체 마감
     */
    private List<Long> resolveCourseIds(AttendanceClosureRequest request) {
        if (request.getCourseId() != null) {
            return List.of(request.getCourseId());
        }

        return courseRepository.findByAcademicYearId(request.getAcademicYearId()).stream()
                .map(Course::getId) // Course 엔티티의 PK getter (프로젝트에 맞게 getCourseId일 수도 있음)
                .collect(Collectors.toList());
    }

    /**
     * 학년/반 조건이 있을 때 enrollmentIds를 필터링한다.
     * - (grade,classNo) 둘 다 있으면 해당 학년/반 소속 학생만
     * - grade만 있으면 해당 학년만
     * - 조건이 없으면 enrollments 전체를 그대로 사용
     */
    private List<Long> filterEnrollmentIdsByClass(AttendanceClosureRequest request, List<Enrollment> enrollments) {

        // 학년/반 필터가 없으면 전체 enrollmentId 반환
        if (request.getGrade() == null && request.getClassNo() == null) {
            return enrollments.stream()
                    .map(Enrollment::getEnrollmentId)
                    .collect(Collectors.toList());
        }

        // enrollment에서 학생ID만 뽑아서 student_detail 조회용 리스트로 만든다.
        List<Long> studentIds = enrollments.stream()
                .map(Enrollment::getStudentId)
                .distinct()
                .collect(Collectors.toList());

        // student_detail에서 조건에 맞는 학생ID만 골라낸다.
        List<Long> matchedStudentIds;

        if (request.getGrade() != null && request.getClassNo() != null) {
            // 학년 + 반 둘 다 필터
            String classNo = String.valueOf(request.getClassNo());

            matchedStudentIds = studentDetailRepository
                    .findByIdInAndGradeAndClassNo(studentIds, request.getGrade(), classNo)
                    .stream()
                    .map(detail -> detail.getId()) // StudentDetail PK가 아니라 studentId를 반환해야 하는 구조일 수도 있음
                    .collect(Collectors.toList());

        } else if (request.getGrade() != null) {
            // 학년만 필터
            matchedStudentIds = studentDetailRepository
                    .findByIdInAndGrade(studentIds, request.getGrade())
                    .stream()
                    .map(detail -> detail.getId())
                    .collect(Collectors.toList());

        } else {
            // 반만 필터하는 케이스는 현재 미지원(필요하면 else if로 추가)
            matchedStudentIds = List.of();
        }

        if (matchedStudentIds.isEmpty()) {
            return List.of();
        }

        // 최종: enrollment 중에서 "조건에 맞는 학생"의 enrollmentId만 반환
        return enrollments.stream()
                .filter(enrollment -> matchedStudentIds.contains(enrollment.getStudentId()))
                .map(Enrollment::getEnrollmentId)
                .collect(Collectors.toList());
    }
}