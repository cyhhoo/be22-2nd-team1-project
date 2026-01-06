package com.mycompany.project.user.entity;

import com.mycompany.project.schedule.entity.Subject;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tbl_teacher_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TeacherDetail {

    @Id
    @Column(name = "teacher_id")
    private Long id;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @Column(name = "homeroom_grade")
    private Integer homeroomGrade;

    @Column(name = "homeroom_class_no")
    private Integer homeroomClassNo;
}