package com.mycompany.project.user.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "tbl_student_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StudentDetail {

    @Id
    @Column(name = "student_id")
    private Long id; // PK and FK

    @MapsId // Share PK with User
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User user;

    @NotNull
    @Min(1)
    @Max(3)
    @Column(name = "student_grade")
    private Integer grade;

    @Column(name = "student_class_no")
    private String classNo;

    @NotNull
    @Positive
    @Column(name = "student_no")
    private Integer studentNo;
}