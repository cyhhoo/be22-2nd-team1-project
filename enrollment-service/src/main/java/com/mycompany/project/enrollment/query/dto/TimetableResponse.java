package com.mycompany.project.enrollment.query.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TimetableResponse {

  private Long courseId; // 강좌 상세 조회를 위해 필요

  private String courseName;   // 과목명
  private String dayOfWeek;    // 요일 (MON, TUE...)
  private Integer period;      // 교시 (1, 2...)
  private String classroom;    // 강의실 (A101)
  private String teacherName;// 선생님

}
