package com.mycompany.project.exception;

import com.mycompany.project.common.response.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
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

    return ResponseEntity.status(java.util.Objects.requireNonNull(errorCode.getStatus())).body(response);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
    logger.error("IllegalArgumentException: ", e);

    ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;
    ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), e.getMessage());

    return ResponseEntity.status(java.util.Objects.requireNonNull(errorCode.getStatus())).body(response);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
    logger.error("NoResourceFoundException: ", e);

    ErrorCode errorCode = ErrorCode.RESOURCE_NOT_FOUND;
    ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());

    return ResponseEntity.status(java.util.Objects.requireNonNull(errorCode.getStatus())).body(response);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
    logger.error("HttpMessageNotReadableException: ", e);

    ErrorCode errorCode = ErrorCode.INVALID_JSON_FORMAT;
    ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
    return ResponseEntity.status(java.util.Objects.requireNonNull(errorCode.getStatus())).body(response);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException e) {
    logger.error("BusinessException: ", e);

    ErrorCode errorCode = e.getErrorCode();
    ApiResponse<Void> response = ApiResponse.failure(errorCode.getCode(), errorCode.getMessage());
    return ResponseEntity.status(java.util.Objects.requireNonNull(errorCode.getStatus())).body(response);
  }

  /**
   * Handles validation failure (400 Bad Request)
   * Returns detailed field error messages in Map format
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
      MethodArgumentNotValidException e) {
    logger.error("Validation Error: ", e);

    Map<String, String> errors = new HashMap<>();
    e.getBindingResult().getAllErrors().forEach((error) -> {
      String fieldName = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(fieldName, errorMessage);
    });

    ErrorCode errorCode = ErrorCode.INVALID_INPUT_VALUE;

    ApiResponse<Map<String, String>> response = ApiResponse.<Map<String, String>>builder()
        .success(false)
        .errorCode(errorCode.getCode())
        .message("Invalid input values.")
        .data(errors)
        .timestamp(java.time.LocalDateTime.now())
        .build();

    return ResponseEntity.status(java.util.Objects.requireNonNull(errorCode.getStatus())).body(response);
  }
}
