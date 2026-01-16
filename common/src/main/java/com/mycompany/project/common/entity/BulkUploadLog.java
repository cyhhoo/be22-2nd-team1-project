package com.mycompany.project.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tbl_bulk_upload_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class BulkUploadLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "upload_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id")
    private AttachFile file;

    @Column(name = "admin_id")
    private Long adminId; // AdminDetailEntity 李몄“ ?쒓굅 -> ID 李몄“濡?蹂寃?

    @Enumerated(EnumType.STRING)
    @Column(name = "upload_type", nullable = false)
    private UploadType uploadType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UploadStatus status;

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

    @Builder.Default
    @Column(name = "request_id", nullable = false, length = 50)
    private String requestId = UUID.randomUUID().toString().substring(0, 36);

    public void startProcessing() {
        this.status = UploadStatus.PROCESSING;
    }

    public void complete(int successCount, int failCount, String errorLog) {
        this.status = UploadStatus.COMPLETED;
        this.successCount = successCount;
        this.failCount = failCount;
        this.totalCount = successCount + failCount;
        this.errorLog = errorLog;
        this.finishedAt = LocalDateTime.now();
    }

    public void fail(String errorLog) {
        this.status = UploadStatus.FAILED;
        this.errorLog = errorLog;
        this.finishedAt = LocalDateTime.now();
    }
}