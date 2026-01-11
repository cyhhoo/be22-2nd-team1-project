package com.mycompany.project.enrollment.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_enrollment")
@Getter
@NoArgsConstructor
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
    private EnrollmentStatus status = EnrollmentStatus.ACTIVE;

    @Builder
    public Enrollment(Long userId, Long courseId) {
        this.userId = userId;
        this.courseId = courseId;
        this.status = EnrollmentStatus.ACTIVE;
    }

    public void cancel() {
        this.status = EnrollmentStatus.CANCELED;
    }

    public enum EnrollmentStatus {
        ACTIVE, CANCELED
    }
}
