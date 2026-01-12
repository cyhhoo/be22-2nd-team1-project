package com.mycompany.project.course.service;

import com.mycompany.project.enrollment.entity.Enrollment;
import com.mycompany.project.user.command.domain.aggregate.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class NotificationService {

    /**
     * 알림 전송 (단순 로그 출력으로 모사)
     * - 실제 운영 환경에서는 Email, SMS, Push Notification 발송 로직이 들어갑니다.
     * - 알림 전송이 메인 트랜잭션(강좌 생성/삭제 등)에 영향을 주지 않도록
     * REQUIRES_NEW 등으로 트랜잭션을 분리하거나 비동기(@Async)로 처리하는 것이 좋습니다.
     */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void send(List<Enrollment> enrollments, String message) {
        if (enrollments == null || enrollments.isEmpty()) {
            log.info("[Notification] 보낼 대상이 없습니다.");
            return;
        }

        log.info("========== [알림 발송 시작] ==========");
        log.info("내용: {}", message);

        for (Enrollment enrollment : enrollments) {
            try {
                User student = enrollment.getStudentDetailId().getUser();
                if (student != null) {
                    // 실제로는 student.getEmail(), student.getPhone() 등을 사용하여 발송
                    log.info(" > To: {} (ID:{}, Email:{}) | Message: {}",
                            student.getName(),
                            student.getUserId(),
                            student.getEmail(),
                            message);
                }
            } catch (Exception e) {
                log.error("알림 발송 중 오류 발생 - Enrollment ID: {}", enrollment.getEnrollmentId(), e);
            }
        }
        log.info("========== [알림 발송 종료] ==========");
    }
}
