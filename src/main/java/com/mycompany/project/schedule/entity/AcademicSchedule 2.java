package com.mycompany.project.schedule.entity;

import com.mycompany.project.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@Table(name = "tbl_academic_schedule")
public class AcademicSchedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "academic_year_id")
    private AcademicYear academicYear;

    @Column(nullable = false)
    private LocalDate scheduleDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ScheduleType scheduleType; // ENUM: START, EXAM, HOLIDAY

    @Column(nullable = false)
    private String content; // 일정 내용 (예: "1학기 기말고사 시작")

    private String targetGrade; // ENUM: 1, 2, 3, ALL
}