package com.mycompany.project.attendance.repository;

import com.mycompany.project.attendance.entity.AttendanceCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
/**
 * 출결 코드(AttendanceCode) JPA Repository
 * - 출결 코드 조회/관리용
 * - 기본 코드(PRESENT) 같은 값을 찾을 때 사용
 */
public interface AttendanceCodeRepository extends JpaRepository<AttendanceCode, Long> {

    /**
     * code 값으로 "활성화된" 출결코드 1건 조회
     * - 예: code="PRESENT" 이면서 is_active=1 인 데이터
     */
    Optional<AttendanceCode> findByCodeAndActiveTrue(String code);
}