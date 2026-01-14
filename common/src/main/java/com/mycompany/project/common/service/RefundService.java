package com.mycompany.project.common.service;

import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class RefundService {

    public void processRefund(Long studentId, Long courseId, String reason) {
        log.info("[Refund Service] Refund processed for Student ID: {}, Course ID: {}, Reason: {}", studentId, courseId,
                reason);
    }
}
