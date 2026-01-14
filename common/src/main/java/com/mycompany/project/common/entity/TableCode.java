package com.mycompany.project.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_table_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TableCode { // BaseEntity 상속 불필요 (created_at만 있음) hoặc 직접 추가

    @Id
    @Column(name = "table_code_id")
    private Integer id;

    @Column(name = "table_name", nullable = false, length = 50)
    private String tableName;

    @Column(length = 100)
    private String description;
    
    // createdAt 등은 필요시 추가
}