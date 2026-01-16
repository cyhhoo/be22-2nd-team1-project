package com.mycompany.project.course.command.domain.aggregate;

import com.mycompany.project.exception.BusinessException;
import com.mycompany.project.exception.ErrorCode;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_course")
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "course_id")
  private Long courseId;

  @Column(name = "teacher_detail_id", nullable = false)
  private Long teacherDetailId;

  @Column(name = "academic_year_id")
  private Long academicYearId;

  @Column(name = "subject_id")
  private Long subjectId;

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "course_type", nullable = false, length = 20)
  private CourseType courseType;

  @Column(name = "max_capacity", nullable = false)
  private Integer maxCapacity;

  @Builder.Default
  @Column(name = "current_count", nullable = false)
  private Integer currentCount = 0;

  @Builder.Default
  @Column(nullable = false)
  private Integer tuition = 0;

  @Builder.Default
  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private CourseStatus status = CourseStatus.OPEN;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "rejection_reason", length = 500)
  private String rejectionReason;

  @Builder.Default
  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CourseTimeSlot> timeSlots = new ArrayList<>();

  public void addTimeSlot(CourseTimeSlot timeSlot) {
    this.timeSlots.add(timeSlot);
    timeSlot.assignCourse(this);
  }

  public void increaseCurrentCount() {
    if (this.currentCount >= this.maxCapacity) {
      this.status = CourseStatus.CLOSED;
      throw new BusinessException(ErrorCode.COURSE_CAPACITY_FULL);
    }
    this.currentCount++;
    if (this.currentCount.equals(this.maxCapacity)) {
      this.status = CourseStatus.CLOSED;
    }
  }

  public void decreaseCurrentCount() {
    if (this.currentCount > 0) {
      this.currentCount--;
    }
    if (this.currentCount < this.maxCapacity && this.status == CourseStatus.CLOSED) {
      this.status = CourseStatus.OPEN;
    }
  }

  public void updateCourseInfo(String name, CourseType courseType, Integer maxCapacity, Integer tuition,
      Long subjectId, Long academicYearId, Long teacherDetailId) {
    if (name != null)
      this.name = name;
    if (courseType != null)
      this.courseType = courseType;
    if (maxCapacity != null && maxCapacity > 0)
      this.maxCapacity = maxCapacity;
    if (tuition != null && tuition >= 0)
      this.tuition = tuition;
    if (subjectId != null)
      this.subjectId = subjectId;
    if (academicYearId != null)
      this.academicYearId = academicYearId;
    if (teacherDetailId != null)
      this.teacherDetailId = teacherDetailId;
  }

  public void changeStatus(CourseStatus status) {
    if (status != null) {
      this.status = status;
    }
  }

  public void update(String name, CourseType courseType, Integer maxCapacity, Integer tuition,
      Long subjectId, Long academicYearId, Long teacherDetailId, CourseStatus status) {
    updateCourseInfo(name, courseType, maxCapacity, tuition, subjectId, academicYearId, teacherDetailId);
    changeStatus(status);
  }

  public void setRejectionReason(String rejectionReason) {
    this.rejectionReason = rejectionReason;
  }
}
