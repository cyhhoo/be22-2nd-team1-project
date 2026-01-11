package com.mycompany.project.enrollment.query.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.enrollment.repository.EnrollmentMapper;

@Service
@RequiredArgsConstructor
public class EnrollmentQueryService {
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;
}
