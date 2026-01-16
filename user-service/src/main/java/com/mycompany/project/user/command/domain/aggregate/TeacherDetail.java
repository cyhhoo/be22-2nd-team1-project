package com.mycompany.project.user.command.domain.aggregate;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
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

    @NotNull
    @Column(name = "subject_id")
    private Long subjectId; // Refers to subjectId (instead of direct Entity reference)

    @Min(1)
    @Max(3)
    @Column(name = "homeroom_grade")
    private Integer homeroomGrade;

    @Positive
    @Column(name = "homeroom_class_no")
    private Integer homeroomClassNo;

    public void updateInfo(Long subjectId, Integer homeroomGrade, Integer homeroomClassNo) {
        if (subjectId != null)
            this.subjectId = subjectId;
        if (homeroomGrade != null)
            this.homeroomGrade = homeroomGrade;
        if (homeroomClassNo != null)
            this.homeroomClassNo = homeroomClassNo;
    }
}