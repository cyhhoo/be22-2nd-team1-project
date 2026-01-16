package com.mycompany.project.user.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "tbl_admin_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class AdminDetail {

    @Id
    @Column(name = "admin_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "admin_id")
    private User user;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('LEVEL_1', 'LEVEL_5') DEFAULT 'LEVEL_5'")
    private AdminLevel level; // AdminLevel enum (LEVEL_1, LEVEL_5, etc.)

}