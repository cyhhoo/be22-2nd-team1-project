package com.mycompany.project.common.response;

/**
 * Common Response Handler Class
 * - Unified API response format
 *   Allows client to process responses in a consistent manner
 */

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse<T> {

  private boolean success; // Request success status
  private T data; // Actual data (used on success)
  private String errorCode; // Error code (used on failure)
  private String message; // Error message (on failure)
  private LocalDateTime timestamp; // Response creation time

  /** Success response static factory method */
  public static <T> ApiResponse<T> success(T data) {
    return ApiResponse.<T>builder().success(true).data(data).timestamp(LocalDateTime.now()).build();
  }

  /** Failure response static factory method */
  public static <T> ApiResponse<T> failure(String errorCode, String message) {
    return ApiResponse.<T>builder().success(false).errorCode(errorCode).message(message).timestamp(LocalDateTime.now())
        .build();
  }
}
