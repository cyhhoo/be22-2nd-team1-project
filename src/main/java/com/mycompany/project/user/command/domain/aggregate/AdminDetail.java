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
    @Column(nullable = false, columnDefinition = "ENUM('1', '5') DEFAULT '5'")
    private AdminLevel level; // Enum 필요 (LEVEL_1, LEVEL_5 등)

    public enum AdminLevel {
        LEVEL_1("1"), LEVEL_5("5");

        private final String value;

        AdminLevel(String value) {
            this.value = value;
        }
    }
}