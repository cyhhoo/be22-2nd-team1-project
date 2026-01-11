package com.mycompany.project.course.entity;

import  jakarta.persistence.*;
import lombok.*;
import lombok.extern.slf4j.Slf4j;


@Entity
@Table(name = "tbl_course_time_slot")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Slf4j
public class CourseTimeSlot {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slot_id")
    private Long id;


    // N:1 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    // 요일은 Enum으로 만들어도 되고, 간단하면 String으로 써도 됨 (여기선 String 처리)
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

    // 연관관계 설정을 위한 내부 메서드
    protected void assignCourse(Course course) {
        this.course = course;
    }
}