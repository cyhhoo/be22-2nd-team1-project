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

  private String courseName;      // 怨쇰ぉ紐?
  private String teacherName;   // ?좎깮???깊븿
  private String status;          //

  private String enrollmentDate;  // "2025-03-02" (String?쇰줈 ?щ㎎?낅맂 ?좎쭨)

}
