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

    private static final EnrollmentStatus ENROLLMENT_STATUS_APPLIED = EnrollmentStatus.APPLIED;

    private final AttendanceRepository attendanceRepository;
    private final AttendanceClosureRepository attendanceClosureRepository;
    private final AcademicYearRepository academicYearRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentDetailRepository studentDetailRepository;
    private final UserRepository userRepository;

    @Transactional
    public void closeAttendances(AttendanceClosureRequest request) {
        validateRequest(request);
        ensureAdmin(request.getUserId());

        // 마감 범위에 맞는 날짜 구간을 산출한다.
        LocalDate fromDate = resolveFromDate(request);
        LocalDate toDate = resolveToDate(request, fromDate);

        List<Long> courseIds = resolveCourseIds(request);
        if (courseIds.isEmpty()) {
            return;
        }

        // 강좌 범위 → 수강신청 → 출결 대상 범위를 축소한다.
        List<Enrollment> enrollments = enrollmentRepository.findByCourseIdInAndStatus(courseIds, ENROLLMENT_STATUS_APPLIED);
        if (enrollments.isEmpty()) {
            return;
        }

        List<Long> enrollmentIds = filterEnrollmentIdsByClass(request, enrollments);
        if (enrollmentIds.isEmpty()) {
            return;
        }

        List<Attendance> allAttendances = attendanceRepository.findByEnrollmentIdInAndClassDateBetween(
            enrollmentIds, fromDate, toDate
        );
        if (allAttendances.isEmpty()) {
            return;
        }
        boolean hasUnconfirmed = allAttendances.stream()
            .anyMatch(attendance -> attendance.getState() != AttendanceState.CONFIRMED
                && attendance.getState() != AttendanceState.CLOSED);
        if (hasUnconfirmed) {
            throw new IllegalStateException("확정되지 않은 출결이 있어 마감할 수 없습니다.");
        }

        List<Attendance> attendances = allAttendances.stream()
            .filter(attendance -> attendance.getState() == AttendanceState.CONFIRMED)
            .collect(Collectors.toList());
        if (!attendances.isEmpty()) {
            for (Attendance attendance : attendances) {
                attendance.close();
            }
            attendanceRepository.saveAll(attendances);
        }

        // 마감 이력 저장은 별도로 기록한다.
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

    private void validateRequest(AttendanceClosureRequest request) {
        if (request == null || request.getAcademicYearId() == null || request.getScopeType() == null
            || request.getScopeValue() == null || request.getUserId() == null) {
            throw new IllegalArgumentException("필수 파라미터가 누락되었습니다.");
        }
    }

    private void ensureAdmin(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 사용자입니다."));
        if (user.getRole() != Role.ADMIN) {
            throw new IllegalStateException("관리자만 마감 처리할 수 있습니다.");
        }
    }

    private LocalDate resolveFromDate(AttendanceClosureRequest request) {
        if (request.getScopeType() == ScopeType.MONTH) {
            YearMonth yearMonth = YearMonth.parse(request.getScopeValue());
            return yearMonth.atDay(1);
        }
        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
            .orElseThrow(() -> new IllegalArgumentException("학년도/학기 정보가 없습니다."));
        return academicYear.getStartDate();
    }

    private LocalDate resolveToDate(AttendanceClosureRequest request, LocalDate fromDate) {
        if (request.getScopeType() == ScopeType.MONTH) {
            YearMonth yearMonth = YearMonth.parse(request.getScopeValue());
            return yearMonth.atEndOfMonth();
        }
        AcademicYear academicYear = academicYearRepository.findById(request.getAcademicYearId())
            .orElseThrow(() -> new IllegalArgumentException("학년도/학기 정보가 없습니다."));
        return academicYear.getEndDate();
    }

    private List<Long> resolveCourseIds(AttendanceClosureRequest request) {
        if (request.getCourseId() != null) {
            return List.of(request.getCourseId());
        }
        return courseRepository.findByAcademicYearId(request.getAcademicYearId()).stream()
            .map(Course::getId)
            .collect(Collectors.toList());
    }

    private List<Long> filterEnrollmentIdsByClass(AttendanceClosureRequest request, List<Enrollment> enrollments) {
        if (request.getGrade() == null && request.getClassNo() == null) {
            return enrollments.stream()
                .map(Enrollment::getEnrollmentId)
                .collect(Collectors.toList());
        }

        // 학년/반 필터가 있으면 학생 상세를 기준으로 대상 학생을 추린다.
        List<Long> studentIds = enrollments.stream()
            .map(Enrollment::getStudentId)
            .distinct()
            .collect(Collectors.toList());

        List<Long> matchedStudentIds;
        if (request.getGrade() != null && request.getClassNo() != null) {
            String classNo = String.valueOf(request.getClassNo());
            matchedStudentIds = studentDetailRepository
                .findByIdInAndGradeAndClassNo(studentIds, request.getGrade(), classNo)
                .stream()
                .map(detail -> detail.getId())
                .collect(Collectors.toList());
        } else if (request.getGrade() != null) {
            matchedStudentIds = studentDetailRepository
                .findByIdInAndGrade(studentIds, request.getGrade())
                .stream()
                .map(detail -> detail.getId())
                .collect(Collectors.toList());
        } else {
            matchedStudentIds = List.of();
        }

        if (matchedStudentIds.isEmpty()) {
            return List.of();
        }

        return enrollments.stream()
            .filter(enrollment -> matchedStudentIds.contains(enrollment.getStudentId()))
            .map(Enrollment::getEnrollmentId)
            .collect(Collectors.toList());
    }
}
