package com.mycompany.project.attendance.entity;

import com.mycompany.project.attendance.entity.enums.ScopeType;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 출결 마감 이력(Entity)
 * - 어떤 범위(월/학기)를 언제 누가 마감했는지 기록
 * - 실제로 출결을 CLOSED로 변경할지(일괄 업데이트) 여부는 서비스 정책으로 결정
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@AllArgsConstructor
@Builder
@Table(name = "tbl_attendance_closure")
public class AttendanceClosure {

    /**
     * 마감이력 PK
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "closure_id")
    private Long id;

    /**
     * 학년도/학기 FK(현재는 Long으로만 보관)
     */
    @Column(name = "academic_year_id", nullable = false)
    private Long academicYearId;

    /**
     * 범위 타입(MONTH/SEMESTER)
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "scope_type", nullable = false, length = 20)
    private ScopeType scopeType;

    /**
     * 범위 값
     * - MONTH: "2025-09"
     * - SEMESTER: "2025-1"
     */
    @Column(name = "scope_value", nullable = false, length = 20)
    private String scopeValue;

    /**
     * 선택 조건(학년) - null이면 전체 학년
     */
    @Column(name = "grade")
    private Integer grade;

    /**
     * 선택 조건(반) - null이면 전체 반
     */
    @Column(name = "class_no")
    private Integer classNo;

    /**
     * 선택 조건(강좌) - null이면 전체 강좌
     */
    @Column(name = "course_id")
    private Long courseId;

    /**
     * 마감일시
     */
    @Column(name = "closed_at", nullable = false)
    private LocalDateTime closedAt;

    /**
     * 마감 관리자 user_id
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 생성자
     * - 필터(학년/반/강좌)는 선택값이라 null 허용
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
     * INSERT 직전 마감일시 기본값 세팅
     */
    @PrePersist
    void onCreate() {
        if (this.closedAt == null) {
            this.closedAt = LocalDateTime.now();
        }
    }
}