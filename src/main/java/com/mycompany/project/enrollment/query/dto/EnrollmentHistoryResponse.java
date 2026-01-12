package com.mycompany.project.enrollment.query.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class EnrollmentHistoryResponse {

  private Long enrollmentId;

  private Long courseId;

  private String courseName;      // 과목명
  private String teacherName;   // 선생님 성함
  private String status;          //

  private String enrollmentDate;  // "2025-03-02" (String으로 포맷팅된 날짜)

}
