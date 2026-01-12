package com.mycompany.project.exception;

import com.mycompany.project.common.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

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

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
    logger.error("AccountInactiveException: ", e);

    ErrorCode errorCode = e.getErrorCode();
    ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }

  /**
   * Valid 유효성 검사 실패 시 (400 Bad Request)
   * 상세 필드 에러 메세지를 Map 형태로 반환 하는 핸들러 메서드
   * @param e
   * @return
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException e) {
    logger.error("Validation Error: ", e);

    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

    // ApiResponse.failure() 대신 data에 errors 맵을 담아서 반환
    ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
        .success(false)
        .errorCode(errorCode.getCode())
        .message("입력값이 올바르지 않습니다.")
        .data(errors) // 상세 에러 정보 포함
        .timestamp(java.time.LocalDateTime.now())
        .build();

    return ResponseEntity.status(errorCode.getStatus()).body(response);
  }
}
