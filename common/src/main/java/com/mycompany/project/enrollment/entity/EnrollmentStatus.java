package com.mycompany.project.enrollment.entity;

public enum EnrollmentStatus {
  APPLIED             // 수강 신청 성공
  , CANCELED          // 수강 신청 취소
  , FORCED_CANCELED   // 관리자에 의해 강제 취소됨
}