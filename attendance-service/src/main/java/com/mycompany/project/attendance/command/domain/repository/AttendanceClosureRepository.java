package com.mycompany.project.attendance.command.domain.repository;

import com.mycompany.project.attendance.command.domain.aggregate.AttendanceClosure;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * 異쒓껐 留덇컧 ?대젰(AttendanceClosure) JPA Repository
 * - 留덇컧 ?대젰 ???議고쉶(CUD ?ы븿)??
 * - 議고쉶??MyBatis濡????섎룄 ?덉?留? ??μ? JPA濡?媛꾨떒??泥섎━?섎뒗 ?⑸룄
 */
public interface AttendanceClosureRepository extends JpaRepository<AttendanceClosure, Long> {
}