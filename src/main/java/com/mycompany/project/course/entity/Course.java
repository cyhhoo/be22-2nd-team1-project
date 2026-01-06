package com.mycompany.project.course.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tbl_course")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자가 필수 (protected 권장)
@EntityListeners(AuditingEntityListener.class) // 생성일 자동 주입
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "course_id")
  private Long id;

  // 다른 도메인(Teacher, Subject 등)은 ID로만 참조 (느슨한 결합)
  @Column(name = "teacher_detail_id")
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

  @Column(name = "current_count")
  private Integer currentCount = 0; // 기본값 0

  @Column(name = "tuition")
  private Integer tuition = 0; // 기본값 0

  @Enumerated(EnumType.STRING)
  @Column(name = "status", length = 20)
  private CourseStatus status = CourseStatus.OPEN; // 기본값 OPEN

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  // 양방향 연관관계 (Course -> CourseTimeSlot)
  // cascade = ALL: 강좌가 삭제되면 시간표도 같이 삭제
  // orphanRemoval = true: 리스트에서 제거되면 DB에서도 삭제
  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CourseTimeSlot> timeSlots = new ArrayList<>();

  @Builder
  public Course(Long teacherDetailId, Long academicYearId, Long subjectId, String name,
                CourseType courseType, Integer maxCapacity, Integer tuition) {
    this.teacherDetailId = teacherDetailId;
    this.academicYearId = academicYearId;
    this.subjectId = subjectId;
    this.name = name;
    this.courseType = courseType;
    this.maxCapacity = maxCapacity;
    this.tuition = tuition;
  }

  // 연관관계 편의 메서드 (객체 양쪽에 값을 넣어줌)
  public void addTimeSlot(CourseTimeSlot timeSlot) {
    this.timeSlots.add(timeSlot);
    timeSlot.assignCourse(this);
  }
}