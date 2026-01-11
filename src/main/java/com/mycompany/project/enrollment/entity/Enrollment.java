package com.mycompany.project.enrollment.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_enrollment", uniqueConstraints = {
        @UniqueConstraint(name = "uk_enrollment", columnNames = { "course_id", "user_id" })
})
@Getter
@NoArgsConstructor
@EntityListeners(org.springframework.data.jpa.domain.support.AuditingEntityListener.class)
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long enrollmentId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "course_id", nullable = false)
    private Long courseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private EnrollmentStatus status = EnrollmentStatus.APPLIED;

    @org.springframework.data.annotation.CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.LocalDateTime createdAt;

    @Column(name = "cancellation_reason")
    private String cancellationReason; // 취소 사유 (강제 취소 시 필수)

    @Builder
    public Enrollment(Long userId, Long courseId) {
        this.userId = userId;
        this.courseId = courseId;
        this.status = EnrollmentStatus.APPLIED;
    }

    public void cancel() {
        this.status = EnrollmentStatus.CANCELED;
    }

    public void forceCancel(String reason) {
        this.status = EnrollmentStatus.FORCED_CANCELED;
        this.cancellationReason = reason;
    }

    public enum EnrollmentStatus {
        APPLIED, CANCELED, FORCED_CANCELED
    }
}
