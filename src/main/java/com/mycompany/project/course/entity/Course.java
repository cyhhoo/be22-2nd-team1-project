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

  /**
   * 강좌 정보 수정 메서드
   * <p>
   * 비즈니스 로직을 엔티티 내에 캡슐화하여,
   * Service에서는 이 메서드를 호출(메시지 전송)하는 방식으로 수정합니다.
   * </p>
   *
   * @param name            강좌명
   * @param courseType      강좌 타입
   * @param maxCapacity     최대 수강 인원
   * @param tuition         수강료
   * @param subjectId       과목 ID
   * @param academicYearId  학년 ID
   * @param teacherDetailId 교사 ID
   * @param status          강좌 상태
   */
  public void update(String name, CourseType courseType, Integer maxCapacity, Integer tuition,
      Long subjectId, Long academicYearId, Long teacherDetailId, CourseStatus status) {
    // null이 아닌 값만 수정하거나, 정책에 따라 null도 허용하여 덮어쓸 수 있습니다.
    // 여기서는 입력된 값으로 전면 수정하는 방식을 적용합니다.
    if (name != null)
      this.name = name;
    if (courseType != null)
      this.courseType = courseType;
    if (maxCapacity != null && maxCapacity > 0)
      this.maxCapacity = maxCapacity; // 0이하 방지
    if (tuition != null && tuition >= 0)
      this.tuition = tuition; // 음수 방지
    if (subjectId != null)
      this.subjectId = subjectId;
    if (academicYearId != null)
      this.academicYearId = academicYearId;
    if (teacherDetailId != null)
      this.teacherDetailId = teacherDetailId;
    if (status != null)
      this.status = status;
  }
}