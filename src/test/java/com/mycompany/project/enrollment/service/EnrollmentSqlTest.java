package com.mycompany.project.enrollment.service;

import com.mycompany.project.enrollment.query.dto.EnrollmentHistoryResponse;
import com.mycompany.project.enrollment.query.dto.TimetableResponse;
import com.mycompany.project.enrollment.query.service.EnrollmentQueryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:mariadb://localhost:3306/test?characterEncoding=UTF-8&serverTimezone=UTC",
    "spring.datasource.username=swcamp",  // 본인 ID
    "spring.datasource.password=swcamp",  // 본인 비번
    "spring.datasource.driver-class-name=org.mariadb.jdbc.Driver",
    "spring.jpa.hibernate.ddl-auto=none", // JPA가 테이블 건드리지 못하게 함
    "spring.sql.init.mode=always"
})
// 👇 중요: 테스트 시작 전에 스키마 만들고 -> 데이터 넣음
@Sql(scripts = {"/schema-test.sql", "/data-test.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class EnrollmentSqlTest {

  @Autowired
  private EnrollmentQueryService enrollmentQueryService;

  private final Long TEST_STUDENT_ID = 20L;

  @Test
  @DisplayName("MariaDB 기반 - 내 수강 내역 조회")
  void getMyHistory_SqlTest() {
    // When
    List<EnrollmentHistoryResponse> history = enrollmentQueryService.getMyHistory(TEST_STUDENT_ID);

    // Then
    assertThat(history).isNotEmpty();
    assertThat(history.get(0).getCourseName()).isEqualTo("자바 프로그래밍");
    assertThat(history.get(0).getTeacherName()).isEqualTo("김선생");
    System.out.println(">>> 조회된 수강 내역: " + history.get(0));
  }

  @Test
  @DisplayName("MariaDB 기반 - 내 시간표 조회")
  void getMyTimetable_SqlTest() {
    // When
    List<TimetableResponse> timetable = enrollmentQueryService.getMyTimetable(TEST_STUDENT_ID);

    // Then
    assertThat(timetable).hasSize(2);
    assertThat(timetable.get(0).getDayOfWeek()).isEqualTo("MON");
    assertThat(timetable.get(1).getDayOfWeek()).isEqualTo("WED");
    System.out.println(">>> 조회된 시간표: " + timetable);
  }
}