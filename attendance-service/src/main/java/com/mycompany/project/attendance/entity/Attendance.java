package com.mycompany.project.attendance.entity;

import com.mycompany.project.attendance.entity.enums.AttendanceState;
import jakarta.persistence.*;
import lombok.*;

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
@AllArgsConstructor
@Builder
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
    @Column(name = "period", nullable = false)
    private byte period;

    /** 사유(선택) */
    @Column(name = "reason", length = 255)
    private String reason;

    /** 출결 상태 (SAVED / CONFIRMED / CLOSED) */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
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
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** 출결코드 FK (ID로만 보관) */
    @Column(name = "attendance_code_id", nullable = false)
    private Long attendanceCodeId;

    /** 수강신청 FK (ID로만 보관) */
    @Column(name = "enrollment_id", nullable = false)
    private Long enrollmentId;

    /**
     * 신규 출결 생성용 생성자
     * - 신규 생성 시 기본 상태는 SAVED
     */
    @Builder
    public Attendance(LocalDate classDate,
                      byte period,
                      Long attendanceCodeId,
                      Long enrollmentId,
                      Long savedBy,
                      String reason) {
        this.classDate = classDate;
        this.period = period;
        this.attendanceCodeId = attendanceCodeId;
        this.enrollmentId = enrollmentId;
        this.savedBy = savedBy;
        this.reason = reason;
        this.state = AttendanceState.SAVED;
    }

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        this.savedAt = (this.savedAt == null) ? now : this.savedAt;
        this.createdAt = (this.createdAt == null) ? now : this.createdAt;
        this.updatedAt = (this.updatedAt == null) ? now : this.updatedAt;
        this.state = (this.state == null) ? AttendanceState.SAVED : this.state;
    }

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

    /** 교사 저장/수정 처리 (CONFIRMED/CLOSED면 불가) */
    public void saveByTeacher(Long teacherId, Long newAttendanceCodeId, String newReason) {
        if (this.state == AttendanceState.CONFIRMED || this.state == AttendanceState.CLOSED) {
            throw new IllegalStateException("확정 또는 마감된 출결은 수정할 수 없습니다.");
        }
        this.attendanceCodeId = newAttendanceCodeId;
        this.reason = newReason;
        this.savedBy = teacherId;
        this.savedAt = LocalDateTime.now();
        this.state = AttendanceState.SAVED;
    }

    /** 확정 처리 (CLOSED면 불가) */
    public void confirm(Long teacherId) {
        ensureNotClosed();
        this.state = AttendanceState.CONFIRMED;
        this.confirmedBy = teacherId;
        this.confirmedAt = LocalDateTime.now();
    }

    /** 마감 처리 */
    public void close() {
        this.state = AttendanceState.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    /** 정정요청 승인 반영 (정책상 CLOSED도 허용) */
    public void applyCorrection(Long newAttendanceCodeId) {
        this.attendanceCodeId = newAttendanceCodeId;
    }

    private void ensureNotClosed() {
        if (this.state == AttendanceState.CLOSED) {
            throw new IllegalStateException("마감된 출결은 수정할 수 없습니다.");
        }
    }
}