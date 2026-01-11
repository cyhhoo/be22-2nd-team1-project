package com.mycompany.project.exception;

import com.mycompany.project.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
    logger.error("Unhandled Exception: ", e);

    ErrorCode errorCode = ErrorCode.INTERNAL_SERVER_ERROR;
    ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());

    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
    logger.error("IllegalArgumentException: ", e);

    ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
    ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());

    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
    logger.error("NoResourceFoundException: ", e);

    ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;
    ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
    
    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    logger.error("HttpMessageNotReadableException: ", e);

    ErrorCode errorCode = ErrorCode.INVALID_JSON_FORMAT;
    ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }

  @ExceptionHandler(AccountInactiveException.class)
  public ResponseEntity<ApiResponse<Void>> handleAccountInactiveException(AccountInactiveException e) {
    logger.error("AccountInactiveException: ", e);

    ErrorCode errorCode = ErrorCode.ACCOUNT_INACTIVE;
    ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }
}
