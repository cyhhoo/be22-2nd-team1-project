package com.mycompany.project.enrollment.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import com.mycompany.project.enrollment.mapper.EnrollmentMapper;

@Service
@RequiredArgsConstructor
public class EnrollmentService {
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;
}
