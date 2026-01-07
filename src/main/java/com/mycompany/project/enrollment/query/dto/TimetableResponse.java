package com.mycompany.project.enrollment.query.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TimetableResponse {

  private String courseName;   // 과목명
  private String dayOfWeek;    // 요일 (MON, TUE...)
  private Integer period;      // 교시 (1, 2...)
  private String classroom;    // 강의실 (A101)
  private String teacherName;// 선생님

}
