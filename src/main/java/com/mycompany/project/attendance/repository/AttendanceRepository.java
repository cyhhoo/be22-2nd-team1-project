package com.mycompany.project.attendance.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.mycompany.project.attendance.entity.Attendance;
import com.mycompany.project.attendance.entity.enums.AttendanceState;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    Optional<Attendance> findByEnrollmentIdAndClassDateAndPeriod(Long enrollmentId, LocalDate classDate, byte period);

    List<Attendance> findByEnrollmentIdInAndClassDateAndPeriod(Collection<Long> enrollmentIds, LocalDate classDate, byte period);

    long countByEnrollmentIdInAndClassDateAndPeriod(Collection<Long> enrollmentIds, LocalDate classDate, byte period);

    List<Attendance> findByEnrollmentIdInAndClassDateBetween(Collection<Long> enrollmentIds,
                                                             LocalDate fromDate,
                                                             LocalDate toDate);

    List<Attendance> findByEnrollmentIdInAndClassDateBetweenAndState(Collection<Long> enrollmentIds,
                                                                     LocalDate fromDate,
                                                                     LocalDate toDate,
                                                                     AttendanceState state);
}
