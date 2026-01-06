package com.mycompany.project.common.response;

import com.mycompany.project.common.exception.ErrorCode;
import lombok.Getter;

@Getter
public class ErrorResponse {

  private final String code;
  private final String description;
  private final String detail;

  public ErrorResponse(ErrorCode errorCode, String detail) {
    this.code = errorCode.getCode();
    this.description = errorCode.getDescription();
    this.detail = detail;
  }

  public static ErrorResponse of(ErrorCode errorCode, String detail){
    return new ErrorResponse(errorCode,detail);
  }
}
