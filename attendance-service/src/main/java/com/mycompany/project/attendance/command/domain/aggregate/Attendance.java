package com.mycompany.project.attendance.command.domain.aggregate;

import com.mycompany.project.attendance.command.domain.aggregate.enums.AttendanceState;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Attendance Entity
 * - Stores actual student attendance status for specific lecture date/period
 * - Query uses MyBatis, write uses JPA (Entity is needed for write model)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
@Table(name = "tbl_attendance")
public class Attendance {
    /** Attendance PK (AUTO_INCREMENT PRIMARY KEY recommended) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attendance_id")
    private Long id;

    /** Lecture date */
    @Column(name = "class_date", nullable = false)
    private LocalDate classDate;

    /** Period (1~8) */
    @Column(name = "period", nullable = false)
    private byte period;

    /** Reason (optional) */
    @Column(name = "reason", length = 255)
    private String reason;

    /** Attendance state (SAVED / CONFIRMED / CLOSED) */
    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false, length = 20)
    private AttendanceState state;

    /** Teacher who saved */
    @Column(name = "saved_by", nullable = false)
    private Long savedBy;

    /** Save timestamp */
    @Column(name = "saved_at", nullable = false)
    private LocalDateTime savedAt;

    /** Teacher who confirmed */
    @Column(name = "confirmed_by")
    private Long confirmedBy;

    /** Confirm timestamp */
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    /** Close timestamp */
    @Column(name = "closed_at")
    private LocalDateTime closedAt;

    /** Created timestamp */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /** Attendance code FK (ID only) */
    @Column(name = "attendance_code_id", nullable = false)
    private Long attendanceCodeId;

    /** Enrollment FK (ID only) */
    @Column(name = "enrollment_id", nullable = false)
    private Long enrollmentId;

    /**
     * Constructor for new attendance creation
     * - Initial state is SAVED
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

    // ===== Business methods (instead of Setter) =====

    /** Update reason (blocked if CLOSED) */
    public void updateReason(String reason) {
        ensureNotClosed();
        this.reason = reason;
    }

    /** Change attendance code (blocked if CLOSED) */
    public void changeAttendanceCode(Long newAttendanceCodeId) {
        ensureNotClosed();
        this.attendanceCodeId = newAttendanceCodeId;
    }

    /** Teacher save (blocked if CONFIRMED/CLOSED) */
    public void saveByTeacher(Long teacherId, Long newAttendanceCodeId, String newReason) {
        if (this.state == AttendanceState.CONFIRMED || this.state == AttendanceState.CLOSED) {
            throw new IllegalStateException("Cannot modify confirmed or closed attendance.");
        }
        this.attendanceCodeId = newAttendanceCodeId;
        this.reason = newReason;
        this.savedBy = teacherId;
        this.savedAt = LocalDateTime.now();
        this.state = AttendanceState.SAVED;
    }

    /** Confirm (blocked if CLOSED) */
    public void confirm(Long teacherId) {
        ensureNotClosed();
        this.state = AttendanceState.CONFIRMED;
        this.confirmedBy = teacherId;
        this.confirmedAt = LocalDateTime.now();
    }

    /** Close attendance */
    public void close() {
        this.state = AttendanceState.CLOSED;
        this.closedAt = LocalDateTime.now();
    }

    /** Apply correction request (allowed for CLOSED by policy) */
    public void correctionByAdmin(Long adminId, Long newAttendanceCodeId) {
        this.attendanceCodeId = newAttendanceCodeId;
        this.confirmedBy = adminId;
        this.confirmedAt = LocalDateTime.now();
    }

    private void ensureNotClosed() {
        if (this.state == AttendanceState.CLOSED) {
            throw new IllegalStateException("Cannot modify closed attendance.");
        }
    }
}