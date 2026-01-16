package com.mycompany.project.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // Common
  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_001", "Internal server error occurred."),
  INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_002", "Invalid input value."),
  RESOURCE_NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_003", "Resource not found."),
  INVALID_JSON_FORMAT(HttpStatus.BAD_REQUEST, "COMMON_004", "Invalid JSON format."),

  // User & Auth
  ACCOUNT_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_001", "Account ID or password does not match."),
  ACCOUNT_INACTIVE(HttpStatus.FORBIDDEN, "ACCOUNT_002", "Account is inactive."),
  ACCOUNT_LOCKED(HttpStatus.FORBIDDEN, "ACCOUNT_003", "Account is locked. Please contact administrator."),
  INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "ACCOUNT_004", "Account ID or password does not match."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "ACCOUNT_005", "Invalid Refresh Token."),
  TOKEN_NOT_FOUND(HttpStatus.NOT_FOUND, "ACCOUNT_006", "Token not found or expired."),
  LOGIN_REQUIRED(HttpStatus.UNAUTHORIZED, "ACCOUNT_007", "Login is required for this service."),
  ALREADY_ACTIVE_ACCOUNT(HttpStatus.BAD_REQUEST, "ACCOUNT_008", "Account is already active."),
  USER_INFO_MISMATCH(HttpStatus.BAD_REQUEST, "ACCOUNT_009", "User information does not match."),
  INVAID_AUTH_CODE(HttpStatus.UNAUTHORIZED, "ACCOUNT_010", "Authentication code does not match."),

  // Attendance (ATT)
  REQUIRED_PARAMETER_MISSING(HttpStatus.CONFLICT, "ATT_001", "Course ID / Lecture Date / User ID is required."),
  ATTENDANCE_CODE_INACTIVE(HttpStatus.BAD_REQUEST, "ATT_002", "Cannot use inactive attendance code."),
  ATTENDANCE_ITEMS_EMPTY(HttpStatus.CONFLICT, "ATT_003", "No attendance items to process."),
  ATTENDANCE_CANNOT_MODIFY_CONFIRMED_OR_CLOSED(HttpStatus.NOT_FOUND, "ATT_004",
      "Cannot modify confirmed/closed attendance."),
  ATT_COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_005", "Course not found."),
  DEFAULT_ATTENDANCE_CODE_NOT_FOUND(HttpStatus.BAD_REQUEST, "ATT_006", "Default attendance code (PRESENT) not found."),
  ATTENDANCE_ITEM_INFO_MISSING(HttpStatus.FORBIDDEN, "ATT_007", "Attendance item information is missing."),
  NOT_ENROLLED_IN_COURSE(HttpStatus.NOT_FOUND, "ATT_008", "Not enrolled in this course."),
  ATTENDANCE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_009", "Attendance code not found."),
  ATTENDANCE_NOTHING_TO_CONFIRM(HttpStatus.CONFLICT, "ATT_010", "No attendance to confirm."),
  ATTENDANCE_CANNOT_CONFIRM_WITH_UNFILLED_ITEMS(HttpStatus.CONFLICT, "ATT_011",
      "Cannot confirm with unfilled attendance items."),
  ATTENDANCE_CANNOT_CONFIRM_CLOSED(HttpStatus.FORBIDDEN, "ATT_012", "Cannot confirm closed attendance."),
  ONLY_COURSE_TEACHER_ALLOWED(HttpStatus.FORBIDDEN, "ATT_013", "Only course teacher can process this."),
  HOMEROOM_PERMISSION_REQUIRED(HttpStatus.NOT_FOUND, "ATT_014", "Access permission required."),
  ATTENDANCE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_015", "Attendance not found."),
  REQUESTED_ATTENDANCE_CODE_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_016", "Requested attendance code not found."),
  CORRECTION_REQUEST_NOT_FOUND(HttpStatus.BAD_REQUEST, "ATT_017", "Correction request not found."),
  REJECT_REASON_REQUIRED(HttpStatus.NOT_FOUND, "ATT_018", "Rejection reason is required."),
  USER_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_019", "User not found."),
  ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "ATT_020", "Enrollment information not found."),
  COURSE_INFO_NOT_FOUND(HttpStatus.CONFLICT, "ATT_021", "Course information not found."),
  CORRECTION_ONLY_CONFIRMED_OR_CLOSED_ALLOWED(HttpStatus.CONFLICT, "ATT_022",
      "Only confirmed or closed attendance can request correction."),
  CORRECTION_ALREADY_IN_PROGRESS(HttpStatus.FORBIDDEN, "ATT_023", "Correction request already in progress."),
  CORRECTION_ADMIN_ONLY(HttpStatus.FORBIDDEN, "ATT_024", "Only admin can process this."),
  CORRECTION_TEACHER_ONLY_CREATE(HttpStatus.FORBIDDEN, "ATT_025", "Only teacher can create correction request."),
  CORRECTION_ONLY_COURSE_TEACHER_CREATE(HttpStatus.NOT_FOUND, "ATT_026",
      "Only course teacher can create correction request."),
  ACADEMIC_TERM_INFO_MISSING(HttpStatus.FORBIDDEN, "ATT_027", "Academic year/term information is missing."),
  CLOSURE_ADMIN_ONLY(HttpStatus.CONFLICT, "ATT_028", "Only admin can process closure."),
  CLOSURE_CANNOT_CLOSE_WITH_UNCONFIRMED(HttpStatus.CONFLICT, "ATT_029", "Cannot close with unconfirmed attendance."),
  CORRECTION_ALREADY_PROCESSED(HttpStatus.CONFLICT, "ATT_030", "Correction request already processed."),
  ATTENDANCE_CANNOT_MODIFY_CONFIRMED_OR_CLOSED_DOMAIN(HttpStatus.CONFLICT, "ATT_031",
      "Cannot modify confirmed or closed attendance."),
  ATTENDANCE_CANNOT_MODIFY_CLOSED(HttpStatus.CONFLICT, "ATT_032", "Cannot modify closed attendance."),
  ATTENDANCE_REQUIRED_PARAMS_MISSING(HttpStatus.BAD_REQUEST, "ATT_033",
      "Course ID / Lecture Date / User ID / Attendance items are required."),
  ATTENDANCE_ITEM_INVALID_FORMAT(HttpStatus.BAD_REQUEST, "ATT_034",
      "Attendance ID, requested code ID, request reason, and requester ID are required."),
  ATTENDANCE_STUDENT_LIST_EMPTY(HttpStatus.BAD_REQUEST, "ATT_035", "Correction request ID and admin ID are required."),
  ATTENDANCE_DATE_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "ATT_036",
      "Academic year / term / scope type / scope value / user ID are required."),

  // Course (COURSE)
  STUDENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_001", "Student not found."),
  COURSE_NOT_OPEN(HttpStatus.BAD_REQUEST, "COURSE_002", "Course not found."),
  COURSE_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_003", "Course not found."),
  COURSE_BAD_REQUEST(HttpStatus.BAD_REQUEST, "COURSE_004", "Invalid request. Please check and try again."),
  COURSE_CONDITION_MISMATCH(HttpStatus.CONFLICT, "COURSE_005",
      "Input value matches but cannot be processed by current business logic."),
  TEACHER_NOT_FOUND(HttpStatus.BAD_REQUEST, "COURSE_006", "Teacher not found."),
  INSTRUCTOR_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_014", "Instructor not found."),
  ACADEMIC_YEAR_NOT_FOUND(HttpStatus.BAD_REQUEST, "COURSE_007", "Academic year not found."),
  COURSE_ENROLLMENT_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_008", "Enrollment history not found."),
  COURSE_CHANGE_REQUEST_NOT_FOUND(HttpStatus.NOT_FOUND, "COURSE_009", "Change request not found."),
  INSTRUCTOR_TIMETABLE_CONFLICT(HttpStatus.CONFLICT, "COURSE_010", "Instructor has conflicting schedule."),
  COURSE_NOT_WAITING_APPROVAL(HttpStatus.BAD_REQUEST, "COURSE_011", "No approval/rejection permission."),
  STUDENT_REQUIRED_COURSE_CONFLICT(HttpStatus.CONFLICT, "COURSE_012",
      "Student already has required course at this time."),
  STUDENT_MEMO_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "COURSE_013", "Student memo not allowed."),

  // Enrollment (ENROLL)
  ALREADY_ENROLLED(HttpStatus.BAD_REQUEST, "ENROLL_001", "Already enrolled in this course."),
  COURSE_CAPACITY_FULL(HttpStatus.BAD_REQUEST, "ENROLL_002", "Course capacity is full."),
  TIME_CONFLICT(HttpStatus.BAD_REQUEST, "ENROLL_003", "Schedule conflict with enrolled courses."),
  MAX_CREDITS_EXCEEDED(HttpStatus.BAD_REQUEST, "ENROLL_004", "Maximum credits exceeded."),
  NOT_YOUR_ENROLLMENT(HttpStatus.BAD_REQUEST, "ENROLL_005", "Can only cancel your own enrollment."),
  ENROLL_NOT_FOUND(HttpStatus.NOT_FOUND, "ENROLL_006", "Enrollment not found."),
  ALREADY_CANCELED(HttpStatus.BAD_REQUEST, "ENROLL_007", "Already canceled enrollment."),

  // Cart (CART)
  ALREADY_IN_CART(HttpStatus.BAD_REQUEST, "CART_001", "Course already in cart."),
  CART_EMPTY(HttpStatus.BAD_REQUEST, "CART_002", "Cart is empty. Cannot enroll."),

  // Reservation (RES)
  FACILITY_NOT_FOUND(HttpStatus.NOT_FOUND, "RES_001", "Facility not found."),
  FACILITY_NOT_AVAILABLE(HttpStatus.BAD_REQUEST, "RES_002", "Facility not available for reservation."),
  FACILITY_RESTRICTED_PERIOD(HttpStatus.FORBIDDEN, "RES_003", "Facility usage restricted period."),
  FACILITY_OUT_OF_OPERATION_HOURS(HttpStatus.BAD_REQUEST, "RES_004", "Outside facility operation hours."),
  NOT_FACILITY_ADMIN(HttpStatus.FORBIDDEN, "RES_005", "Only facility admin can approve/reject."),
  INVALID_RESERVATION_TIME(HttpStatus.BAD_REQUEST, "RES_101", "Reservation must be in 1-hour units (on the hour)."),
  RESERVATION_TIME_OUT_OF_RANGE(HttpStatus.BAD_REQUEST, "RES_102",
      "Reservation time must be 06:00-21:00, start by 20:00."),
  RESERVATION_NOT_FOUND(HttpStatus.NOT_FOUND, "RES_201", "Reservation not found."),
  NOT_RESERVATION_OWNER(HttpStatus.FORBIDDEN, "RES_202", "Can only cancel/modify your own reservation."),
  NOT_MY_RESERVATION(HttpStatus.FORBIDDEN, "RES_203", "Not your reservation."),
  RESERVED_TIME_CONFLICT(HttpStatus.CONFLICT, "RES_301", "Time already reserved."),
  ALREADY_APPROVED_RESERVATION(HttpStatus.CONFLICT, "RES_302", "Approved reservation already exists at this time."),
  RESERVATION_APPROVED_CANNOT_CANCEL(HttpStatus.BAD_REQUEST, "RES_401", "Cannot cancel approved reservation."),
  RESERVATION_ONLY_WAITING_CAN_CHANGE(HttpStatus.BAD_REQUEST, "RES_402", "Only waiting status can be changed."),
  RESERVATION_ALREADY_PROCESSED(HttpStatus.BAD_REQUEST, "RES_403", "Reservation already processed."),
  FACILITY_NAME_DUPLICATED(HttpStatus.CONFLICT, "FAC_001", "Facility name must be unique."),
  FACILITY_UPDATE_FORBIDDEN(HttpStatus.FORBIDDEN, "FAC_003", "No update permission."),
  FACILITY_DELETE_FORBIDDEN(HttpStatus.FORBIDDEN, "FAC_004", "No delete permission."),
  INVALID_OPERATION_TIME(HttpStatus.BAD_REQUEST, "FAC_005", "Start time must be before end time."),
  INVALID_FACILITY_STATUS(HttpStatus.BAD_REQUEST, "FAC_006", "Status must be AVAILABLE or UNAVAILABLE."),

  // Subject (SUBJECT)
  SUBJECT_NOT_FOUND(HttpStatus.NOT_FOUND, "SUBJECT_001", "Subject not found."),
  SUBJECT_ALREADY_EXISTS(HttpStatus.CONFLICT, "SUBJECT_002", "Subject name already exists."),

  // Schedule (SCHEDULE)
  SCHEDULE_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE_001", "Schedule not found."),
  SCHEDULE_ACADEMIC_YEAR_NOT_FOUND(HttpStatus.NOT_FOUND, "SCHEDULE_002", "Academic year term not found.");

  private final HttpStatus status;
  private final String code;
  private final String message;

}