package com.mycompany.project.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tbl_teacher_detail")
@Getter
@NoArgsConstructor
public class TeacherDetail {

    @Id
    @Column(name = "teacher_id")
    private Long teacherId;

    @Column(name = "subject_id")
    private Long subjectId;

    @Column(name = "homeroom_grade")
    private Integer homeroomGrade;

    @Column(name = "homeroom_class_no")
    private Integer homeroomClassNo;
}
