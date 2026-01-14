package com.mycompany.project.course.entity;

import com.mycompany.project.user.command.domain.aggregate.TeacherDetail;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 기본 생성자가 필수 (protected 권장)
@EntityListeners(AuditingEntityListener.class) // 생성일 자동 주입
public class Course {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "course_id")
  private Long courseId;

  // 다른 도메인(Teacher, Subject 등)은 ID로만 참조 (느슨한 결합)
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "teacher_detail_id", nullable = false)
  private TeacherDetail teacherDetail;

  @Column(name = "academic_year_id")
  private Long academicYearId; // FK: tbl_academic_year

  @Column(name = "subject_id")
  private Long subjectId; // FK: tbl_subject

  @Column(name = "name", nullable = false, length = 100)
  private String name;

  @Enumerated(EnumType.STRING)
  @Column(name = "course_type", nullable = false, length = 20)
  private CourseType courseType;

  @Column(name = "max_capacity", nullable = false)
  private Integer maxCapacity;

  @Builder.Default
  @Column(name = "current_count", nullable = false)
  private Integer currentCount = 0; // 기본값 0

  @Builder.Default
  @Column(nullable = false)
  private Integer tuition = 0;

  // [수정] 기본값 OPEN 적용
  @Builder.Default
  @Enumerated(EnumType.STRING) // Enum은 DB 저장 시 String 권장
  @Column(name = "status", length = 20)
  private CourseStatus status = CourseStatus.OPEN;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @Column(name = "rejection_reason", length = 500)
  private String rejectionReason; // 반려 사유

  // 양방향 연관관계 (Course -> CourseTimeSlot)
  // cascade = ALL: 강좌가 삭제되면 시간표도 같이 삭제
  // orphanRemoval = true: 리스트에서 제거되면 DB에서도 삭제
  @Builder.Default
  @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<CourseTimeSlot> timeSlots = new ArrayList<>();

  // teacherDetailId 필드 삭제 (DuplicateMappingException 해결)
  // 대신 teacherDetail.getId()를 활용하도록 헬퍼 메서드 추가

  public Long getTeacherDetailId() {
    return this.teacherDetail != null ? this.teacherDetail.getId() : null;
  }

  // 연관관계 편의 메서드 (객체 양쪽에 값을 넣어줌)
  public void addTimeSlot(CourseTimeSlot timeSlot) {
    this.timeSlots.add(timeSlot);
    timeSlot.assignCourse(this);
  }

  /**
   * 수강 신청 시 호출: 인원 증가
   * 정원이 꽉 찼다면 예외를 터뜨려서 신청을 막습니다.
   */
  public void increaseCurrentCount() {
    // 1. 방어 로직: 이미 꽉 찼는지 확인
    if (this.currentCount >= this.maxCapacity) {
      throw new IllegalStateException("수강 정원이 초과되었습니다.");
    }
    // 2. 상태 변경
    this.currentCount++;
  }

  /**
   * 수강 취소 시 호출: 인원 감소
   * 0명 미만으로 내려가지 않도록 보호합니다.
   */
  public void decreaseCurrentCount() {
    if (this.currentCount > 0) {
      this.currentCount--;
    }
  }

  /**
   * 강좌 정보 수정 (상태 변경 제외)
   */
  public void updateCourseInfo(String name, CourseType courseType, Integer maxCapacity, Integer tuition,
      Long subjectId, Long academicYearId, TeacherDetail teacherDetail) {
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
    if (teacherDetail != null)
      this.teacherDetail = teacherDetail;
  }

  /**
   * 강좌 상태 변경
   */
  public void changeStatus(CourseStatus status) {
    if (status != null) {
      this.status = status;
    }
  }

  /**
   * 통합 업데이트 메서드 (정보 수정 + 상태 변경)
   */
  public void update(String name, CourseType courseType, Integer maxCapacity, Integer tuition,
      Long subjectId, Long academicYearId, TeacherDetail teacherDetail, CourseStatus status) {
    updateCourseInfo(name, courseType, maxCapacity, tuition, subjectId, academicYearId, teacherDetail);
    changeStatus(status);
  }

  public void setRejectionReason(String rejectionReason) {
    this.rejectionReason = rejectionReason;
  }

}