package com.mycompany.project.attendance.command.domain.aggregate;

import com.mycompany.project.attendance.command.domain.aggregate.enums.CorrectionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 異쒓껐 ?뺤젙?붿껌(Entity)
 * - 援먯궗媛 異쒓껐肄붾뱶 蹂寃쎌쓣 ?붿껌?섍퀬, 愿由ъ옄媛 ?뱀씤/諛섎젮
 * - before/requested 異쒓껐肄붾뱶??FK(Long)濡쒕쭔 ????꾩옱???곌?愿怨??앸왂)
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
@Table(name = "tbl_attendance_correction_request")
public class AttendanceCorrectionRequest {

    /** ?뺤젙?붿껌 PK (AUTO_INCREMENT PRIMARY KEY 沅뚯옣) */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    /** 蹂寃???異쒓껐肄붾뱶 FK */
    @Column(name = "before_attendance_code_id", nullable = false)
    private Long beforeAttendanceCodeId;

    /** 蹂寃??붿껌 異쒓껐肄붾뱶 FK */
    @Column(name = "requested_attendance_code_id", nullable = false)
    private Long requestedAttendanceCodeId;

    /** ?뺤젙 ?ъ쑀(湲??띿뒪??媛?? */
    @Lob
    @Column(name = "request_reason", nullable = false)
    private String requestReason;

    /** 泥섎━ ?곹깭 */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CorrectionStatus status;

    /** ?붿껌??援먯궗) */
    @Column(name = "requested_by", nullable = false)
    private Long requestedBy;

    /** ?붿껌?쇱떆 */
    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    /** 泥섎━??愿由ъ옄) */
    @Column(name = "decided_by")
    private Long decidedBy;

    /** 泥섎━?쇱떆 */
    @Column(name = "decided_at")
    private LocalDateTime decidedAt;

    /** 愿由ъ옄 肄붾찘???뱁엳 諛섎젮 ???좎슜) */
    @Lob
    @Column(name = "admin_comment")
    private String adminComment;

    /**
     * PENDING ?쒖떆???좏깮)
     * - status濡쒕룄 異⑸텇?섏?留? UI/?덇굅???붽뎄?ы빆 ?뚮Ц???먮뒗 寃쎌슦媛 ?덉뼱??洹몃?濡?留ㅽ븨
     */
    @Column(name = "pending_flag")
    private Boolean pendingFlag;

    /**
     * 異쒓껐 FK
     * - 二쇱쓽: DB?먯꽌 AUTO_INCREMENT 媛숈? ?ㅼ젙 ?덈? ?ｌ쑝硫?????FK 而щ읆??
     */
    @Column(name = "attendance_id", nullable = false)
    private Long attendanceId;

    /**
     * ?앹꽦???뺤젙?붿껌 ?앹꽦 ??
     * - beforeCode???쒕쾭?먯꽌 Attendance?먯꽌 ?쎌뼱????명똿?섎뒗 寃??덉쟾(?꾨줎??議곗옉 諛⑹?)
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

    /** INSERT 吏곸쟾 湲곕낯媛??명똿 */
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

    // ===== 鍮꾩쫰?덉뒪 硫붿꽌??Setter ??? =====

    /**
     * ?뱀씤 泥섎━
     * - PENDING ?곹깭?먯꽌留?媛??
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
     * 諛섎젮 泥섎━
     * - PENDING ?곹깭?먯꽌留?媛??
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
     * ?곹깭 寃利?
     * - 吏湲덉? IllegalStateException ?덉떆
     * - ? 猷곕?濡?BusinessException(?? CorrectionAlreadyDecidedException)?쇰줈 援먯껜 異붿쿇
     */
    private void ensurePending() {
        if (this.status != CorrectionStatus.PENDING) {
            throw new IllegalStateException("?대? 泥섎━???뺤젙?붿껌?낅땲??");
        }
    }
}
