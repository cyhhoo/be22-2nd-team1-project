package com.mycompany.project.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_student_detail")
@Getter
@NoArgsConstructor
public class StudentDetail {

    @Id
    @Column(name = "student_id")
    private Long studentId;

    @Column(name = "student_grade", nullable = false)
    private Integer studentGrade;

    @Column(name = "student_class_no")
    private String studentClassNo;

    @Column(name = "student_no")
    private Integer studentNo;
}
