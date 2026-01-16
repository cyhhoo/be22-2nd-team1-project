package com.mycompany.project.attendance.command.domain.aggregate;

import com.mycompany.project.attendance.command.domain.aggregate.enums.ScopeType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Attendance Closure Record Entity
 * - Records which scope (e.g., year/semester) is officially closed
 * - Whether to actually change attendance to CLOSED (bulk update) is determined
 * by service policy
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
@Table(name = "tbl_attendance_closure")
public class AttendanceClosure {

    /**
     * Closure record PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "closure_id")
    private Long id;

    /**
     * Academic year/term FK (currently stored as Long only)
     */
    @Column(name = "academic_year_id", nullable = false)
    private Long academicYearId;

    /**
     * Scope type (MONTH/SEMESTER)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type", nullable = false, length = 20)
    private ScopeType scopeType;

    /**
     * Scope value
     * - MONTH: "2025-09"
     * - SEMESTER: "2025-1"
     */
    @Column(name = "scope_value", nullable = false, length = 20)
    private String scopeValue;

    /**
     * Optional filter (grade) - null means all grades
     */
    @Column(name = "grade")
    private Integer grade;

    /**
     * Optional filter (class) - null means all classes
     */
    @Column(name = "class_no")
    private Integer classNo;

    /**
     * Optional filter (course) - null means all courses
     */
    @Column(name = "course_id")
    private Long courseId;

    /**
     * Closure timestamp
     */
    @Column(name = "closed_at", nullable = false)
    private LocalDateTime closedAt;

    /**
     * Closure admin user_id
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Constructor
     * - Filter (grade/class/course) optional values can be null
     */
    @Builder
    public AttendanceClosure(Long academicYearId,
            ScopeType scopeType,
            String scopeValue,
            Integer grade,
            Integer classNo,
            Long courseId,
            Long userId) {
        this.academicYearId = academicYearId;
        this.scopeType = scopeType;
        this.scopeValue = scopeValue;
        this.grade = grade;
        this.classNo = classNo;
        this.courseId = courseId;
        this.userId = userId;
    }

    /**
     * Set default closure timestamp before INSERT
     */
    @PrePersist
    void onCreate() {
        if (this.closedAt == null) {
            this.closedAt = LocalDateTime.now();
        }
    }
}