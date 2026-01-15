package com.mycompany.project.common.aop;

import com.mycompany.project.common.entity.SystemLog;
import com.mycompany.project.common.repository.SystemLogRepository;
import com.mycompany.project.user.command.domain.aggregate.User;
import com.mycompany.project.user.command.domain.repository.UserRepository;
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
  private final UserRepository userRepository;

  @Around("@annotation(com.mycompany.project.common.aop.SystemLoggable)")
  public Object saveSystemLog(ProceedingJoinPoint joinPoint) throws Throwable {

    // 1. 요청 식별자 생성
    String requestId = UUID.randomUUID().toString().substring(0, 8);

    // 2. 메서드 실행
    Object result = joinPoint.proceed();

    try {
      // 3. 어노테이션 정보 가져오기
      MethodSignature signature = (MethodSignature) joinPoint.getSignature();
      SystemLoggable annotation = signature.getMethod().getAnnotation(SystemLoggable.class);

      // 4. 현재 로그인한 사용자 정보 가져오기
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String email = (authentication != null && authentication.isAuthenticated()) ? authentication.getName()
          : "anonymous";

      // "anonymousUser" 인 경우 (로그인 안함) 처리
      User user = null;
      if (!"anonymousUser".equals(email)) {
        user = userRepository.findByEmail(email).orElse(null);
      }

      // 5. Target ID 추출
      Long targetId = 0L;
      if (result instanceof Long) {
        targetId = (Long) result;
      }
      // TODO: DTO 리턴 등 다른 케이스 처리 필요

      // 6. DB 저장
      SystemLog logEntity = SystemLog.builder()
          .user(user)
          .tableCodeId(annotation.tableCodeId())
          .changeType(annotation.type())
          .targetId(targetId)
          .requestId(requestId)
          .build();

      systemLogRepository.save(logEntity);
      log.info("SystemLog saved. Type: {}, TargetId: {}", annotation.type(), targetId);

    } catch (Exception e) {
      log.error("시스템 로그 저장 실패", e);
    }

    return result;
  }
}