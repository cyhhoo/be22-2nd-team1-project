package com.mycompany.project.enrollment.query.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class TimetableResponse {

  private Long courseId; // 媛뺤쥖 ?곸꽭 議고쉶瑜??꾪빐 ?꾩슂

  private String courseName;   // 怨쇰ぉ紐?
  private String dayOfWeek;    // ?붿씪 (MON, TUE...)
  private Integer period;      // 援먯떆 (1, 2...)
  private String classroom;    // 媛뺤쓽??(A101)
  private String teacherName;// ?좎깮??

}
