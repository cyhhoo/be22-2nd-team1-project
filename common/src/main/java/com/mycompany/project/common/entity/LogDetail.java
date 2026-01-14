package com.mycompany.project.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_log_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LogDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_detail_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "log_id")
    private SystemLog systemLog;

    @Column(name = "column_name", nullable = false, length = 50)
    private String columnName;

    @Column(name = "before_value", columnDefinition = "TEXT")
    private String beforeValue;

    @Column(name = "after_value", columnDefinition = "TEXT")
    private String afterValue;
}