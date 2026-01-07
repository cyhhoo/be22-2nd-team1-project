package com.mycompany.project.user.entity;

import jakarta.persistence.*;
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
    private Long id; // PK이자 FK

    @MapsId // User의 PK를 같이 사용
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private User user;

    @Column(name = "student_grade")
    private Integer grade;

    @Column(name = "student_class_no")
    private String classNo;

    @Column(name = "student_no")
    private Integer studentNo;
}