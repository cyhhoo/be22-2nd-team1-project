package com.mycompany.project.enrollment.query.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class CartListResponse {

  private Long cartId;
  private Long courseId;          // ?섏쨷??'?좎껌' 踰꾪듉 ?꾨? ???꾩슂
  private String courseName;
  private String teacherName;

  private String timetableSummary; // ?? "??,2 / ??" (?붾㈃ ?몄쓽???붿빟 ?꾨뱶)

  // 留덇컧 ?꾨컯 ?뺤씤???곗씠??
  private Integer currentCount;   // ?꾩옱 ?좎껌 ?몄썝
  private Integer maxCapacity;    // 理쒕? ?뺤썝

  // [異붽?] CartMapper.xml?먯꽌 議고쉶?섎뒗 ?꾨뱶??
  private String courseType;    // ?댁닔 援щ텇 (?꾧났?꾩닔, 援먯뼇 ??
}
