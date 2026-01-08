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

  // 사용자 에러 코드 추가시 아래에 작성

  // Account
  ACCOUNT_INACTIVE(HttpStatus.FORBIDDEN, "ACCOUNT_001", "계정이 비활성화 상태입니다.");
  private final HttpStatus status;
  private final String code;
  private final String message;

}
