package com.mycompany.project.attendance.command.domain.repository;

import com.mycompany.project.attendance.command.domain.aggregate.AttendanceCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/**
 * 異쒓껐 肄붾뱶(AttendanceCode) JPA Repository
 * - 異쒓껐 肄붾뱶 議고쉶/愿由ъ슜
 * - 湲곕낯 肄붾뱶(PRESENT) 媛숈? 媛믪쓣 李얠쓣 ???ъ슜
 */
public interface AttendanceCodeRepository extends JpaRepository<AttendanceCode, Long> {

    /**
     * code 媛믪쑝濡?"?쒖꽦?붾맂" 異쒓껐肄붾뱶 1嫄?議고쉶
     * - ?? code="PRESENT" ?대㈃??is_active=1 ???곗씠??
     */
    Optional<AttendanceCode> findByCodeAndActiveTrue(String code);
}