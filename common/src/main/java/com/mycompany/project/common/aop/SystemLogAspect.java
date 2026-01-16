package com.mycompany.project.common.aop;

import com.mycompany.project.common.entity.SystemLog;
import com.mycompany.project.common.repository.SystemLogRepository;
import com.mycompany.project.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class SystemLogAspect {

  private final SystemLogRepository systemLogRepository;

  @Around("@annotation(com.mycompany.project.common.aop.SystemLoggable)")
  public Object saveSystemLog(ProceedingJoinPoint joinPoint) throws Throwable {

    String requestId = UUID.randomUUID().toString().substring(0, 8);
    Object result = joinPoint.proceed();

    try {
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      SystemLoggable annotation = signature.getMethod().getAnnotation(SystemLoggable.class);

      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      Long userId = null;

      if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
        userId = ((CustomUserDetails) authentication.getPrincipal()).getUserId();
      }

      SystemLog systemLog = SystemLog.builder()
          .userId(userId)
          .changeType(annotation.type()) // annotation.type() ?몄텧
          .tableCodeId(annotation.tableCodeId())
          .targetId(extractTargetId(joinPoint, result))
          .requestId(requestId)
          .build();

      systemLogRepository.save(systemLog);

    } catch (Exception e) {
      log.error("[SystemLogAspect] Failed to save log: {}", e.getMessage());
    }

    return result;
  }

  private Long extractTargetId(ProceedingJoinPoint joinPoint, Object result) {
    if (result instanceof Long)
      return (Long) result;
    return null;
  }
}