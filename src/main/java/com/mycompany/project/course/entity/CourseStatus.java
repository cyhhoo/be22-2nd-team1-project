package com.mycompany.project.course.entity;

public enum CourseStatus {
  PENDING, // 승인 대기
  OPEN, // 개설
  CLOSED, // 마감
  RUNNING, // 운영중
  CANCELED, // 폐강 == 삭제
  REFUSE // 거절 == 반려
}
