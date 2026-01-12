package com.mycompany.project.enrollment.query.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartListResponse {

  private Long cartId;
  private Long courseId;          // 나중에 '신청' 버튼 누를 때 필요
  private String courseName;
  private String teacherName;

  private String timetableSummary; // 예: "월1,2 / 수3" (화면 편의용 요약 필드)

  // 마감 임박 확인용 데이터
  private Integer currentCount;   // 현재 신청 인원
  private Integer maxCapacity;    // 최대 정원

  // [추가] CartMapper.xml에서 조회하는 필드들
  private String courseType;    // 이수 구분 (전공필수, 교양 등)
}
