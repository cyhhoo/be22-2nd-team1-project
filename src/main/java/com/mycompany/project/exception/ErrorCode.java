package com.mycompany.project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {


  // Common
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_001", "서버 내부 오류가 발생했습니다."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_002", "잘못된 입력값입니다."),
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_003", "리소스를 찾을 수 없습니다."),
  INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "COMMON_004", "잘못된 JSON 형식입니다."),

  // User & Auth (기존 유지 및 보완)
  ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_001", "아이디 또는 비밀번호가 일치하지 않습니다."),
  ACCOUNT_INACTIVE(HttpStatus.FORBIDDEN, "ACCOUNT_002", "계정이 비활성화 상태입니다."),
  ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "ACCOUNT_003", "계정이 잠금되었습니다. 관리자에게 문의하세요."),
  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED,"ACCOUNT_004" , "아이디 또는 비밀번호가 일치하지 않습니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "ACCOUNT_005", "유효하지 않은 Refresh Token 입니다." ),
  TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_006", "존재하지 않거나 만료된 Token 입니다." ),
  

  // Attendance (ATT)
  REQUIRED_PARAMETER_MISSING(HttpStatus.CONFLICT, "ATT_001", "과목ID/수업일/교시/사용자ID는 필수입니다."),
  ATTENDANCE_CODE_INACTIVE(HttpStatus.BAD_REQUEST, "ATT_002", "비활성화된 출결 코드는 사용할 수 없습니다."),
  ATTENDANCE_ITEMS_EMPTY(HttpStatus.CONFLICT, "ATT_003", "저장할 출결 항목이 없습니다."),
  ATTENDANCE_CANNOT_MODIFY_CONFIRMED_OR_CLOSED(HttpStatus.NOT_FOUND, "ATT_004", "확정/마감 상태의 출결은 수정할 수 없습니다."),
  ATT_COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_005", "존재하지 않는 과목입니다."),
  DEFAULT_ATTENDANCE_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ATT_006", "기본 출결 코드(PRESENT)를 찾을 수 없습니다."),
  ATTENDANCE_ITEM_INFO_MISSING(HttpStatus.FORBIDDEN, "ATT_007", "출결 항목 정보가 누락되었습니다."),
  NOT_ENROLLED_IN_COURSE(HttpStatus.NOT_FOUND, "ATT_008", "해당 과목의 수강 신청이 아닙니다."),
  ATTENDANCE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_009", "출결 코드가 존재하지 않습니다."),
  ATTENDANCE_NOTHING_TO_CONFIRM(HttpStatus.CONFLICT, "ATT_010", "확정할 출결 대상이 없습니다."),
  ATTENDANCE_CANNOT_CONFIRM_WITH_UNFILLED_ITEMS(HttpStatus.CONFLICT, "ATT_011", "미입력 출결이 있어 확정할 수 없습니다."),
  ATTENDANCE_CANNOT_CONFIRM_CLOSED(HttpStatus.FORBIDDEN, "ATT_012", "마감된 출결은 확정할 수 없습니다."),
  ONLY_COURSE_TEACHER_ALLOWED(HttpStatus.FORBIDDEN, "ATT_013", "과목 담당 교사만 처리할 수 있습니다."),
  HOMEROOM_PERMISSION_REQUIRED(HttpStatus.NOT_FOUND, "ATT_014", "담임 권한이 없습니다."),
  ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_015", "출결 정보가 존재하지 않습니다."),
  REQUESTED_ATTENDANCE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_016", "요청 출결코드가 존재하지 않습니다."),
  CORRECTION_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "ATT_017", "정정요청이 존재하지 않습니다."),
  REJECT_REASON_REQUIRED(HttpStatus.NOT_FOUND, "ATT_018", "반려 사유는 필수입니다."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_019", "존재하지 않는 사용자입니다."),
  ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_020", "수강신청 정보가 없습니다."),
  COURSE_INFO_NOT_FOUND(HttpStatus.CONFLICT, "ATT_021", "과목 정보가 없습니다."),
  CORRECTION_ONLY_CONFIRMED_OR_CLOSED_ALLOWED(HttpStatus.CONFLICT, "ATT_022", "확정 또는 마감된 출결만 정정요청이 가능합니다."),
  CORRECTION_ALREADY_IN_PROGRESS(HttpStatus.FORBIDDEN, "ATT_023", "이미 처리 중인 정정요청이 있습니다."),
  CORRECTION_ADMIN_ONLY(HttpStatus.FORBIDDEN, "ATT_024", "관리자만 처리할 수 있습니다."),
  CORRECTION_TEACHER_ONLY_CREATE(HttpStatus.FORBIDDEN, "ATT_025", "교사만 정정요청을 생성할 수 있습니다."),
  CORRECTION_ONLY_COURSE_TEACHER_CREATE(HttpStatus.NOT_FOUND, "ATT_026", "과목 담당 교사만 정정요청을 생성할 수 있습니다."),
  ACADEMIC_TERM_INFO_MISSING(HttpStatus.FORBIDDEN, "ATT_027", "학년도/학기 정보가 없습니다."),
  CLOSURE_ADMIN_ONLY(HttpStatus.CONFLICT, "ATT_028", "관리자만 마감 처리할 수 있습니다."),
  CLOSURE_CANNOT_CLOSE_WITH_UNCONFIRMED(HttpStatus.CONFLICT, "ATT_029", "확정되지 않은 출결이 있어 마감할 수 없습니다."),
  CORRECTION_ALREADY_PROCESSED(HttpStatus.CONFLICT, "ATT_030", "이미 처리된 정정요청입니다."),
  ATTENDANCE_CANNOT_MODIFY_CONFIRMED_OR_CLOSED_DOMAIN(HttpStatus.CONFLICT, "ATT_031", "확정 또는 마감된 출결은 수정할 수 없습니다."),
  ATTENDANCE_CANNOT_MODIFY_CLOSED(HttpStatus.CONFLICT, "ATT_032", "마감된 출결은 수정할 수 없습니다."),
  ATTENDANCE_REQUIRED_PARAMS_MISSING(HttpStatus.BAD_REQUEST, "ATT_033", "과목ID/수업일/교시/사용자ID/출결항목(items)은 필수입니다."),
  ATTENDANCE_ITEM_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "ATT_034", "출결ID, 변경요청 출결코드ID, 요청사유, 요청자ID는 필수입니다."),
  ATTENDANCE_STUDENT_LIST_EMPTY(HttpStatus.BAD_REQUEST, "ATT_035", "정정요청ID, 관리자ID는 필수입니다."),
  ATTENDANCE_DATE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "ATT_036", "학년도/학기 /범위타입(scopeType)/범위값(scopeValue)/사용자ID는 필수입니다."),

  // Course (COURSE)
  STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_001", "존재하지 않는 학생입니다."),
  COURSE_NOT_OPEN(HttpStatus.BAD_REQUEST, "COURSE_002", "존재하지 않는 과목입니다."),
  COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_003", "존재하지 않는 강좌입니다."),
  COURSE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COURSE_004", "요청 내용을 고쳐서 다시 보내주세요."),
  COURSE_CONDITION_MISMATCH(HttpStatus.CONFLICT, "COURSE_005", "입력값은 맞지만, 현재 비즈니스 로직상 처리할 수 없습니다."),
  TEACHER_NOT_FOUND(HttpStatus.BAD_REQUEST, "COURSE_006", "존재하지 않는 교사입니다."),
  ACADEMIC_YEAR_NOT_FOUND(HttpStatus.BAD_REQUEST, "COURSE_007", "존재하지 않는 학년입니다."),
  COURSE_ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_008", "수강 신청 내역이 존재하지 않습니다."),
  COURSE_CHANGE_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_009", "존재하지 않는 변경 요청입니다."),
  INSTRUCTOR_TIMETABLE_CONFLICT(HttpStatus.CONFLICT, "COURSE_010", "해당 강사의 다른 수업이 존재합니다."),
  COURSE_NOT_WAITING_APPROVAL(HttpStatus.BAD_REQUEST, "COURSE_011", "승인/반려 권한이 없습니다."),
  STUDENT_REQUIRED_COURSE_CONFLICT(HttpStatus.CONFLICT, "COURSE_012", "해당 학생은 해당 시간에 이미 필수과목이 있어 등록할 수 없습니다."),
  STUDENT_MEMO_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "COURSE_013", "메모 불가능한 학생입니다."),


  // Enrollment (ENROLL)
  ALREADY_ENROLLED(HttpStatus.BAD_REQUEST, "ENROLL_001", "이미 수강 신청된 과목입니다."),
  COURSE_CAPACITY_FULL(HttpStatus.BAD_REQUEST, "ENROLL_002", "수강 정원이 초과되었습니다."),
  TIME_CONFLICT(HttpStatus.BAD_REQUEST, "ENROLL_003", "신청한 과목의 시간이 겹칩니다."),
  MAX_CREDITS_EXCEEDED(HttpStatus.BAD_REQUEST, "ENROLL_004", "최대 수강 가능 학점을 초과했습니다."),
  NOT_YOUR_ENROLLMENT(HttpStatus.BAD_REQUEST, "ENROLL_005", "본인의 수강 내역만 취소할 수 있습니다."),
  ENROLL_NOT_FOUND(HttpStatus.NOT_FOUND, "ENROLL_006", "수강 신청 내역을 찾을 수 없습니다."),

  // Cart (CART)
  ALREADY_IN_CART(HttpStatus.BAD_REQUEST, "CART_001", "이미 장바구니에 담긴 과목입니다."),
  CART_EMPTY(HttpStatus.BAD_REQUEST, "CART_002", "장바구니가 비어 있어 신청할 수 없습니다."),

  // Reservation (RES)
  FACILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "RES_001", "시설이 존재하지 않습니다."),
  FACILITY_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "RES_002", "예약 불가 시설입니다."),
  FACILITY_RESTRICTED_PERIOD(HttpStatus.FORBIDDEN, "RES_003", "시설 이용 제한 기간입니다."),
  FACILITY_OUT_OF_OPERATION_HOURS(HttpStatus.BAD_REQUEST, "RES_004", "시설 운영시간 밖입니다."),
  NOT_FACILITY_ADMIN(HttpStatus.FORBIDDEN, "RES_005", "해당 시설의 관리자만 승인/거부할 수 있습니다."),
  INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "RES_101", "예약은 1시간 단위(정각)로만 가능합니다."),
  RESERVATION_TIME_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "RES_102", "예약 가능 시간은 06:00~21:00이며 시작은 20:00까지 가능합니다."),
  RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RES_201", "예약 없음"),
  NOT_RESERVATION_OWNER(HttpStatus.FORBIDDEN, "RES_202", "본인 예약만 취소/변경 가능"),
  RESERVED_TIME_CONFLICT(HttpStatus.CONFLICT, "RES_301", "이미 예약된 시간입니다."),
  ALREADY_APPROVED_RESERVATION(HttpStatus.CONFLICT, "RES_302", "동일 시간에 이미 승인된 예약이 존재합니다.")
  ;

  private final HttpStatus status;
  private final String code;
  private final String message;

}