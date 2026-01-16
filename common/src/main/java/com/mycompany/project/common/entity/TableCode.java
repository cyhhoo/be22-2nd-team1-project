package com.mycompany.project.common.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_table_code")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TableCode { // BaseEntity ?곸냽 遺덊븘??(created_at留??덉쓬) ho梳톍 吏곸젒 異붽?

    @Id
    @Column(name = "table_code_id")
    private Integer id;

    @Column(name = "table_name", nullable = false, length = 50)
    private String tableName;

    @Column(length = 100)
    private String description;
    
    // createdAt ?깆? ?꾩슂??異붽?
}