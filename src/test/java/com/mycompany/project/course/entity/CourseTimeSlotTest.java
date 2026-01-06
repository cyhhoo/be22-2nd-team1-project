package com.mycompany.project.course.entity;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CourseMappingTest {

  @PersistenceContext
  private EntityManager em;

  @Test
  @DisplayName("Course ↔ CourseTimeSlot 양방향 매핑 및 FK 연결 검증")
  void course_time_slot_mapping_test() {

    // given - Course
    Course course = Course.builder()
        .name("Spring JPA 실전")
        .courseType(CourseType.MANDATORY)
        .maxCapacity(30)
        .tuition(100000)
        .build();

    // given - CourseTimeSlot
    CourseTimeSlot slot1 = CourseTimeSlot.builder()
        .dayOfWeek("MON")
        .period(1)
        .classroom("A-101")
        .build();

    CourseTimeSlot slot2 = CourseTimeSlot.builder()
        .dayOfWeek("WED")
        .period(3)
        .classroom("B-202")
        .build();

    // 연관관계 설정 (편의 메서드 사용)
    course.addTimeSlot(slot1);
    course.addTimeSlot(slot2);

    // when
    em.persist(course);   // cascade = ALL → timeSlot도 같이 persist
    em.flush();
    em.clear();

    Course foundCourse = em.find(Course.class, course.getId());

    // then - Course 검증
    assertThat(foundCourse).isNotNull();
    assertThat(foundCourse.getTimeSlots()).hasSize(2);

    // then - CourseTimeSlot 검증
    CourseTimeSlot foundSlot = foundCourse.getTimeSlots().get(0);
    assertThat(foundSlot.getCourse()).isNotNull();
    assertThat(foundSlot.getCourse().getName()).isEqualTo("Spring JPA 실전");
  }
}
