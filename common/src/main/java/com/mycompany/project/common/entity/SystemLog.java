package com.mycompany.project.common.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "table_code_id")
    private Integer tableCodeId;

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private ChangeType changeType; // com.mycompany.project.common.entity.ChangeType ?ъ슜

    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "request_id", length = 50)
    private String requestId;

    @CreatedDate
    @Column(name = "modifed_at", nullable = false, updatable = false)
    private LocalDateTime modifiedAt;

    @OneToMany(mappedBy = "systemLog", cascade = CascadeType.ALL)
    @Builder.Default
    private List<LogDetail> logDetails = new ArrayList<>();
}