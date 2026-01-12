package com.mycompany.project.attendance.repository;

import com.mycompany.project.attendance.entity.Attendance;
import com.mycompany.project.attendance.entity.enums.AttendanceState;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * 출결(Attendance) JPA Repository
 * - 출결 생성/저장/확정/마감 같은 "쓰기" 작업에서 사용
 * - enrollment_id + 날짜 + 교시 단위로 출결을 찾는 쿼리가 많아서 메서드로 뽑아둔 상태
 */
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    /**
     * 특정 수강신청(enrollment) + 날짜 + 교시에 해당하는 출결 1건 조회
     * - 출석부 생성 시 "이미 만들어졌는지" 중복 체크용
     */
    Optional<Attendance> findByEnrollmentIdAndClassDateAndPeriod(Long enrollmentId, LocalDate classDate, byte period);

    /**
     * 여러 수강신청(enrollmentIds) + 날짜 + 교시에 해당하는 출결 목록 조회
     * - 확정/저장 시 대상 학생들의 출결을 한 번에 가져올 때 사용
     */
    List<Attendance> findByEnrollmentIdInAndClassDateAndPeriod(Collection<Long> enrollmentIds,
                                                               LocalDate classDate,
                                                               byte period);

    /**
     * 여러 수강신청(enrollmentIds) + 날짜 + 교시에 해당하는 출결 개수 조회
     * - "미입력 출결이 있는지" 체크할 때 사용 (count < enrollmentIds.size()면 누락)
     */
    long countByEnrollmentIdInAndClassDateAndPeriod(Collection<Long> enrollmentIds,
                                                    LocalDate classDate,
                                                    byte period);

    /**
     * 여러 수강신청(enrollmentIds) + 날짜 범위(from~to)에 해당하는 출결 목록 조회
     * - 마감 처리 범위(월/학기) 출결을 모아서 가져올 때 사용
     */
    List<Attendance> findByEnrollmentIdInAndClassDateBetween(Collection<Long> enrollmentIds,
                                                             LocalDate fromDate,
                                                             LocalDate toDate);

    /**
     * 여러 수강신청(enrollmentIds) + 날짜 범위(from~to) + 상태(state)에 해당하는 출결 목록 조회
     * - 예: CONFIRMED만 모아서 마감 처리하거나, 상태별 집계/검증할 때 사용
     */
    List<Attendance> findByEnrollmentIdInAndClassDateBetweenAndState(Collection<Long> enrollmentIds,
                                                                     LocalDate fromDate,
                                                                     LocalDate toDate,
                                                                     AttendanceState state);

    /**
     * 특정 수강신청(enrollment) + 출결코드 기준 카운트
     * - 출석/지각/결석 등 코드별 집계에 사용
     */
    long countByEnrollmentIdAndAttendanceCodeId(Long enrollmentId, Long attendanceCodeId);
}
