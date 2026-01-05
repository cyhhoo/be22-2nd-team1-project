package com.mycompany.project.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Slf4j
@Aspect
@Component
public class LogAspect {

  // 모든 Controller 패키지 하위 메소드 실행 시 동작
  @Around("execution(* com.mycompany.project..controller..*(..))")
  public Object logApi(ProceedingJoinPoint joinPoint) throws Throwable {

    // 1. 요청 정보 가져오기
    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    HttpServletRequest request = attributes.getRequest();

    String methodName = joinPoint.getSignature().getName();
    String remoteAddr = request.getRemoteAddr();
    String requestURI = request.getRequestURI();

    log.info(">>> 요청 시작: [{}] {} (Method: {})", request.getMethod(), requestURI, methodName);

    long start = System.currentTimeMillis();

    // 2. 원래 메소드 실행
    try {
      return joinPoint.proceed();
    } finally {
      // 3. 종료 및 소요 시간 기록
      long end = System.currentTimeMillis();
      log.info("<<< 요청 종료: [{}] {} ({}ms 소요)", request.getMethod(), requestURI, (end - start));
    }
  }
}