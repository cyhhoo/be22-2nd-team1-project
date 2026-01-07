package com.mycompany.project.common.entity;

import com.mycompany.project.user.entity.AdminDetail; // AdminDetail 위치에 맞게 import
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_bulk_upload_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class BulkUploadLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "upload_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private AttachFile file;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id") // DB 컬럼명 확인 (admin_id로 변경됨)
    private AdminDetail admin;

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_type", nullable = false)
    private UploadType uploadType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UploadStatus status; // PENDING, PROCESSING, COMPLETED, FAILED

    @Column(name = "total_count")
    private Integer totalCount;

    @Column(name = "success_count")
    private Integer successCount;

    @Column(name = "fail_count")
    private Integer failCount;

    @Column(name = "error_log", columnDefinition = "TEXT")
    private String errorLog;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;

    @Column(name = "request_id", nullable = false, length = 50)
    private String requestId;

}