package com.mycompany.project.common.entity;

import com.mycompany.project.user.command.domain.aggregate.User;
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
@EntityListeners(AuditingEntityListener.class) // created_at 자동 주입용
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 작업자

    @Column(name = "table_code_id")
    private Integer tableCodeId; // 단순 ID 저장 or TableCode Entity 매핑

    @Enumerated(EnumType.STRING)
    @Column(name = "change_type", nullable = false)
    private ChangeType changeType;

    @Column(name = "target_id", nullable = false)
    private Long targetId; // 변경된 대상의 PK

    @Column(name = "request_id", length = 50)
    private String requestId;

    @CreatedDate
    @Column(name = "modifed_at", nullable = false, updatable = false)
    private LocalDateTime modifiedAt;

    // LogDetail과 1:N 관계 (필요 시)
    @OneToMany(mappedBy = "systemLog", cascade = CascadeType.ALL)
    private List<LogDetail> logDetails = new ArrayList<>();

}