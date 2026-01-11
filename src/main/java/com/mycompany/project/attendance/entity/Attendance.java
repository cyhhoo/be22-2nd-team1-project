package com.mycompany.project.attendance.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 출결(Entity)
 * - 실제 학생의 특정 수업일자/교시 출결 상태를 저장
 * - 조회는 MyBatis, 쓰기는 JPA로 하더라도 Entity는 "쓰기 모델"로 필요함
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_attendance")
public class Attendance {

    /** 출결 PK (AUTO_INCREMENT PRIMARY KEY 권장) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    /** 수업일자 */
    @Column(name = "class_date", nullable = false)
    private LocalDate classDate;

    /** 교시(1~8) */
    @Column(nullable = false)
    private byte period;

    /** 사유(선택) */
    @Column(length = 255)
    private String reason;

    /**
     * 출결 상태
     * - EnumType.STRING으로 저장해서 DB Enum과 문자열 매핑
     * - columnDefinition은 스키마 검증(validate)에서 타입 불일치 방지용(환경에 따라 생략 가능)
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('SAVED','CONFIRMED','CLOSED')")
    private AttendanceState state;

    /** 저장한 교사 */
    @Column(name = "saved_by", nullable = false)
    private Long savedBy;

    /** 저장 일시 */
    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    /** 확정한 교사 */
    @Column(name = "confirmed_by")
    private Long confirmedBy;

    /** 확정 일시 */
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    /** 마감 일시 */
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    /** 생성/수정 일시 */
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * FK들
     * - 현재는 연관관계(@ManyToOne) 대신 Long ID로 유지(MVP에서 단순/안전)
     * - 나중에 User/Enrollment 엔티티가 완성되면 연관관계로 리팩토링 가능
     */
    @Column(name = "attendance_code_id", nullable = false)
    private Long attendanceCodeId;

    @Column(name = "enrollment_id", nullable = false)
    private Long enrollmentId;

    /**
     * 생성자(신규 출결 생성)
     * - 신규 생성 시 기본 상태는 SAVED
     */
    public Attendance(LocalDate classDate, byte period, Long attendanceCodeId, Long enrollmentId, Long savedBy, String reason) {
        this.classDate = classDate;
        this.period = period;
        this.attendanceCodeId = attendanceCodeId;
        this.enrollmentId = enrollmentId;
        this.savedBy = savedBy;
        this.reason = reason;
        this.state = AttendanceState.SAVED; // 기본 상태
    }

    /**
     * INSERT 직전 자동 세팅
     * - DB default에 의존하지 않고, 애플리케이션에서도 값 보장
     */
    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        // 저장일시는 생성과 함께 기본 세팅
        this.savedAt = (this.savedAt == null) ? now : this.savedAt;

        // 생성/수정 시간 기본 세팅
        this.createdAt = (this.createdAt == null) ? now : this.createdAt;
        this.updatedAt = (this.updatedAt == null) ? now : this.updatedAt;

        // 상태가 비어있다면 SAVED로
        this.state = (this.state == null) ? AttendanceState.SAVED : this.state;
    }

    /**
     * UPDATE 직전 수정 시간 갱신
     */
    @PreUpdate
    void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // ===== 비즈니스 메서드(Setter 대신) =====

    /** 사유 변경(마감 상태면 금지) */
    public void updateReason(String reason) {
        ensureNotClosed();
        this.reason = reason;
    }

    /** 출결코드 변경(마감 상태면 금지) */
    public void changeAttendanceCode(Long newAttendanceCodeId) {
        ensureNotClosed();
        this.attendanceCodeId = newAttendanceCodeId;
    }

    /**
     * 확정 처리
     * - CLOSED면 확정 불가
     * - 확정자/확정시간 기록
     */
    public void confirm(Long teacherId) {
        ensureNotClosed();
        this.state = AttendanceState.CONFIRMED;
        this.confirmedBy = teacherId;
        this.confirmedAt = LocalDateTime.now();
    }

    /**
     * 마감 처리
     * - 마감 이후에는 변경이 불가능하도록 state를 CLOSED로 변경
     */
    public void close() {
        this.state = AttendanceState.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    /**
     * 마감 상태 체크
     * - 지금은 IllegalStateException 예시
     * - 팀 룰에 맞춰 BusinessException 계열(예: AttendanceClosedException)로 교체 추천
     */
    private void ensureNotClosed() {
        if (this.state == AttendanceState.CLOSED) {
            throw new IllegalStateException("마감된 출결은 수정할 수 없습니다.");
        }
    }
}
