package com.mycompany.project.course.command.domain.aggregate;

import jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

@Entity
@Table(name = "tbl_course_time_slot")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED) // Changed to Protected for consistency
@Slf4j
public class CourseTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long id;

    // N:1 愿怨??ㅼ젙
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // ?붿씪? Enum?쇰줈 留뚮뱾?대룄 ?섍퀬, 媛꾨떒?섎㈃ String?쇰줈 ?⑤룄 ??(?ш린??String 泥섎━)
    @Column(name = "day_of_week", nullable = false, length = 10)
    private String dayOfWeek;

    @Column(name = "period", nullable = false)
    private Integer period;

    @Column(name = "classroom", nullable = false, length = 50)
    private String classroom;

    @Builder
    public CourseTimeSlot(String dayOfWeek, Integer period, String classroom) {
        this.dayOfWeek = dayOfWeek;
        this.period = period;
        this.classroom = classroom;
    }

    // ?곌?愿怨??ㅼ젙???꾪븳 ?대? 硫붿꽌??
    protected void assignCourse(Course course) {
        this.course = course;
    }
}