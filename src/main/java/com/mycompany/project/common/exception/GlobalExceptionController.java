package com.mycompany.project.common.exception;

import com.mycompany.project.common.response.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionController {
  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionController.class);
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e) {
    logger.error("Unhandled Exception: ", e);
    return ResponseEntity
        .status(ErrorCode.INTERNAL_SERVER_ERROR.getStatus())
        .body(ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage()));
  }
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(IllegalArgumentException e) {
    return ResponseEntity
        .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
        .body(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, e.getMessage()));
  }
  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
    return ResponseEntity
        .status(ErrorCode.RESOURCE_NOT_FOUND.getStatus())
        .body(ErrorResponse.of(ErrorCode.RESOURCE_NOT_FOUND, e.getMessage()));
  }
  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    return ResponseEntity
        .status(ErrorCode.INVALID_INPUT_VALUE.getStatus())
        .body(ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, "JSON 형식 오류 또는 잘못된 Enum 값: " + e.getMessage()));
  }
  @ExceptionHandler(AccountInactiveException.class)
  public ResponseEntity<ErrorResponse> handleAccountInactiveException(AccountInactiveException e) {
    return ResponseEntity
        .status(ErrorCode.ACCOUNT_INACTIVE.getStatus())
        .body(ErrorResponse.of(ErrorCode.ACCOUNT_INACTIVE, e.getMessage()));
  }
}
