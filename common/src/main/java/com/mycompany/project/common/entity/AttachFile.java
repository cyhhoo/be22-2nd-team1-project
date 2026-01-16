package com.mycompany.project.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "tbl_file")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class AttachFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "file_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId; // Entity 李몄“ ???ID 李몄“濡?蹂寃?

    @Column(name = "original_name", nullable = false)
    private String originalName;

    @Column(name = "saved_name", nullable = false)
    private String savedName;

    @Column(name = "saved_path", nullable = false)
    private String savedPath;

    @Column(name = "file_size", nullable = false)
    private Long fileSize;

    @Column(name = "file_ext", length = 10, nullable = false)
    private String fileExt;

    private String contentType;

    @Column(name = "table_code_id")
    private Integer tableCodeId;

    @Column(name = "related_id")
    private Long relatedId;

    @Column(name = "is_deleted", length = 1)
    private String isDeleted; // "Y" or "N"

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}