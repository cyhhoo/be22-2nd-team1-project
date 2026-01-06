package com.mycompany.project.attendance.entity;

import com.mycompany.project.attendance.entity.enums.CorrectionStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 출결 정정요청(Entity)
 * - 교사가 출결코드 변경을 요청하고, 관리자가 승인/반려
 * - before/requested 출결코드는 FK(Long)로만 저장(현재는 연관관계 생략)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "tbl_attendance_correction_request")
public class AttendanceCorrectionRequest {

    /** 정정요청 PK (AUTO_INCREMENT PRIMARY KEY 권장) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    /** 변경 전 출결코드 FK */
    @Column(name = "before_attendance_code_id", nullable = false)
    private Long beforeAttendanceCodeId;

    /** 변경 요청 출결코드 FK */
    @Column(name = "requested_attendance_code_id", nullable = false)
    private Long requestedAttendanceCodeId;

    /** 정정 사유(긴 텍스트 가능) */
    @Lob
    @Column(name = "request_reason", nullable = false)
    private String requestReason;

    /** 처리 상태 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('PENDING','APPROVED','REJECTED')")
    private CorrectionStatus status;

    /** 요청자(교사) */
    @Column(name = "requested_by", nullable = false)
    private Long requestedBy;

    /** 요청일시 */
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    /** 처리자(관리자) */
    @Column(name = "decided_by")
    private Long decidedBy;

    /** 처리일시 */
    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    /** 관리자 코멘트(특히 반려 시 유용) */
    @Lob
    @Column(name = "admin_comment")
    private String adminComment;

    /**
     * PENDING 표시용(선택)
     * - status로도 충분하지만, UI/레거시 요구사항 때문에 두는 경우가 있어서 그대로 매핑
     */
    @Column(name = "pending_flag")
    private Boolean pendingFlag;

    /**
     * 출결 FK
     * - 주의: DB에서 AUTO_INCREMENT 같은 설정 절대 넣으면 안 됨(FK 컬럼임)
     */
    @Column(name = "attendance_id", nullable = false)
    private Long attendanceId;

    /**
     * 생성자(정정요청 생성 시)
     * - beforeCode는 서버에서 Attendance에서 읽어와서 세팅하는 게 안전(프론트 조작 방지)
     */
    public AttendanceCorrectionRequest(Long attendanceId,
                                       Long beforeAttendanceCodeId,
                                       Long requestedAttendanceCodeId,
                                       String requestReason,
                                       Long requestedBy) {
        this.attendanceId = attendanceId;
        this.beforeAttendanceCodeId = beforeAttendanceCodeId;
        this.requestedAttendanceCodeId = requestedAttendanceCodeId;
        this.requestReason = requestReason;
        this.requestedBy = requestedBy;
        this.status = CorrectionStatus.PENDING;
        this.pendingFlag = true;
    }

    /** INSERT 직전 기본값 세팅 */
    @PrePersist
    void onCreate() {
        if (this.requestedAt == null) {
            this.requestedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = CorrectionStatus.PENDING;
        }
        if (this.pendingFlag == null) {
            this.pendingFlag = true;
        }
    }

    // ===== 비즈니스 메서드(Setter 대신) =====

    /**
     * 승인 처리
     * - PENDING 상태에서만 가능
     */
    public void approve(Long adminId, String adminComment) {
        ensurePending();
        this.status = CorrectionStatus.APPROVED;
        this.decidedBy = adminId;
        this.decidedAt = LocalDateTime.now();
        this.adminComment = adminComment;
        this.pendingFlag = false;
    }

    /**
     * 반려 처리
     * - PENDING 상태에서만 가능
     */
    public void reject(Long adminId, String adminComment) {
        ensurePending();
        this.status = CorrectionStatus.REJECTED;
        this.decidedBy = adminId;
        this.decidedAt = LocalDateTime.now();
        this.adminComment = adminComment;
        this.pendingFlag = false;
    }

    /**
     * 상태 검증
     * - 지금은 IllegalStateException 예시
     * - 팀 룰대로 BusinessException(예: CorrectionAlreadyDecidedException)으로 교체 추천
     */
    private void ensurePending() {
        if (this.status != CorrectionStatus.PENDING) {
            throw new IllegalStateException("이미 처리된 정정요청입니다.");
        }
    }
}
