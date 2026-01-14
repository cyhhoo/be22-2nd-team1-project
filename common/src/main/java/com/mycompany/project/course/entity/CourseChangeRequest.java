package com.mycompany.project.course.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_course_change_request")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class CourseChangeRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @Enumerated(EnumType.STRING)
    @Column(name = "request_status", length = 20)
    private RequestStatus requestStatus = RequestStatus.PENDING; // 기본값 PENDING

    @Column(name = "reason", length = 500)
    private String reason; // 변경 요청 사유

    @Column(name = "admin_comment", length = 500)
    private String adminComment; // 관리자 코멘트 (반려 시 등)

    // 변경할 데이터 (JSON 직렬화 저장 가정을 위해 String으로 처리하거나, 핵심 필드만 컬럼으로)
    // 여기선 핵심 변경 가능 필드들을 매핑
    @Column(name = "target_max_capacity")
    private Integer targetMaxCapacity;

    @Column(name = "target_tuition")
    private Integer targetTuition;

    @Column(name = "target_teacher_detail_id")
    private Long targetTeacherDetailId;

    // 만약 시간표 변경 등이 포함된다면 복잡해지므로,
    // 여기서는 간단히 정원, 수강료 정도만 변경 요청한다고 가정하거나,
    // 전체 수정 데이터를 JSON String으로 저장하는 방식이 확장성이 좋음.
    // 하지만 DB 호환성(MariaDB JSON) 및 구현 단순화를 위해 핵심 필드 몇 개만 예시로 둠.

    // 강좌명이나 유형은 잘 안바뀌므로, 정원/수강료 변경 요청 위주로 구현

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public enum RequestStatus {
        PENDING, APPROVED, REJECTED
    }

    @Builder
    public CourseChangeRequest(Course course, String reason, Integer targetMaxCapacity, Integer targetTuition,
            Long targetTeacherDetailId) {
        this.course = course;
        this.reason = reason;
        this.targetMaxCapacity = targetMaxCapacity;
        this.targetTuition = targetTuition;
        this.targetTeacherDetailId = targetTeacherDetailId;
    }

    public void approve() {
        this.requestStatus = RequestStatus.APPROVED;
    }

    public void reject(String adminComment) {
        this.requestStatus = RequestStatus.REJECTED;
        this.adminComment = adminComment;
    }
}
