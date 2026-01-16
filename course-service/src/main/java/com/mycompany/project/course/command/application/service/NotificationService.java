package com.mycompany.project.course.command.application.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.List;

@Slf4j
@Service
public class NotificationService {

    public void sendByIds(List<Long> studentIds, String message) {
        log.info("Sending notification to students: {}, Message: {}", studentIds, message);
        // [TODO] Implement real notification logic (e.g., Push, Email, or via
        // Notification Service)
    }

    public void sendToUser(Long userId, String message) {
        log.info("Sending notification to user: {}, Message: {}", userId, message);
    }
}
