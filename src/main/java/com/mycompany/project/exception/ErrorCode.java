package com.mycompany.project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // =================================================================
  // 1. Common (기존 유지)
  // =================================================================
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_001", "서버 내부 오류가 발생했습니다."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_002", "잘못된 입력값입니다."),
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_003", "리소스를 찾을 수 없습니다."),
  INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "COMMON_004", "잘못된 JSON 형식입니다."),


  // =================================================================
  // 2. Account (기존 유지)
  // =================================================================
  ACCOUNT_INACTIVE(HttpStatus.FORBIDDEN, "ACCOUNT_001", "계정이 비활성화 상태입니다."),


  // =================================================================
  // 3. User / Student / Teacher (사용자 관련 - 새로 추가)
  // =================================================================
  STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_001", "존재하지 않는 학생입니다."),
  TEACHER_NOT_FOUND(HttpStatus.NOT_FOUND, "USER_002", "존재하지 않는 교사입니다."),
  INVALID_PERMISSION(HttpStatus.FORBIDDEN, "USER_003", "해당 기능을 수행할 권한이 없습니다."),


  // =================================================================
  // 4. Course (강좌 관련 - 새로 추가)
  // =================================================================
  COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_001", "존재하지 않는 과목입니다."),
  COURSE_NOT_OPEN(HttpStatus.BAD_REQUEST, "COURSE_002", "현재 개설된 강의가 아닙니다."),


  // =================================================================
  // 5. Enrollment (수강신청 관련 - 새로 추가)
  // =================================================================
  // [IllegalState] 비즈니스 로직 위반
  ALREADY_ENROLLED(HttpStatus.BAD_REQUEST, "ENROLL_001", "이미 수강 신청된 과목입니다."),
  COURSE_CAPACITY_FULL(HttpStatus.BAD_REQUEST, "ENROLL_002", "수강 정원이 초과되었습니다."),
  TIME_CONFLICT(HttpStatus.BAD_REQUEST, "ENROLL_003", "신청한 과목의 시간이 기존 시간표와 겹칩니다."),
  MAX_CREDITS_EXCEEDED(HttpStatus.BAD_REQUEST, "ENROLL_004", "최대 수강 가능 학점을 초과했습니다."),
  NOT_YOUR_ENROLLMENT(HttpStatus.BAD_REQUEST, "ENROLL_005", "본인의 수강 내역만 취소할 수 있습니다."),

  // [IllegalArgument] 리소스 부재
  ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ENROLL_006", "수강 신청 내역을 찾을 수 없습니다."),


  // =================================================================
  // 6. Cart (장바구니 관련 - 새로 추가)
  // =================================================================
  ALREADY_IN_CART(HttpStatus.BAD_REQUEST, "CART_001", "이미 장바구니에 담긴 과목입니다."),
  CART_EMPTY(HttpStatus.BAD_REQUEST, "CART_002", "장바구니가 비어 있어 신청할 수 없습니다.");


  private final HttpStatus status;
  private final String code;
  private final String message;

}