package com.mycompany.project.schedule.command.domain.aggregate;

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
  private String content; // Schedule content (e.g., "1st Semester Final Exam")

  @Column(nullable = false)
  private String targetGrade; // Target grade (e.g., 1, 2, 3, ALL)

  @Builder.Default
  @Column(nullable = false)
  private boolean isDeleted = false;

  public void update(
      LocalDate scheduleDate, ScheduleType scheduleType, String content, String targetGrade) {
    this.scheduleDate = scheduleDate;
    this.scheduleType = scheduleType;
    this.content = content;
    this.targetGrade = targetGrade;
  }

  public void delete() {
    this.isDeleted = true;
  }
}