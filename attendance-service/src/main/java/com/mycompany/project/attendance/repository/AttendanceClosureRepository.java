package com.mycompany.project.attendance.repository;

import com.mycompany.project.attendance.entity.AttendanceClosure;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 출결 마감 이력(AttendanceClosure) JPA Repository
 * - 마감 이력 저장/조회(CUD 포함)용
 * - 조회는 MyBatis로 할 수도 있지만, 저장은 JPA로 간단히 처리하는 용도
 */
public interface AttendanceClosureRepository extends JpaRepository<AttendanceClosure, Long> {
}