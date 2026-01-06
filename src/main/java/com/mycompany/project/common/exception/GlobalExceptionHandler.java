package com.mycompany.project.common.exception;

import com.mycompany.project.common.dto.ApiDTO;
import org.springframework.http.HttpStatus;
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
  public ResponseEntity<ApiDTO<Void>> handleException(Exception e) {
      logger.error("Unhandled Exception: ", e);
      return new ResponseEntity<>(ApiDTO.error("서버 내부 오류가 발생했습니다: " + e.getMessage()),
              HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiDTO<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
      return new ResponseEntity<>(ApiDTO.error(e.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ApiDTO<Void>> handleNoResourceFoundException(NoResourceFoundException e) {
      return new ResponseEntity<>(ApiDTO.error("요청한 리소스를 찾을 수 없습니다: " + e.getResourcePath()),
              HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ApiDTO<Void>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
      return new ResponseEntity<>(ApiDTO.error("잘못된 요청 형식입니다. JSON 데이터나 Enum 값을 확인해주세요. " + e.getMessage()),
              HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(AccountInactiveException.class)
  public ResponseEntity<ApiDTO<Void>> handleAccountInactiveException(AccountInactiveException e){
    return new ResponseEntity<>(ApiDTO.error("미활성화된 계정입니다. 인증 번호 입력하여, 계정 활성화 진행하세요"+e.getMessage()),HttpStatus.FORBIDDEN);
  }
}
