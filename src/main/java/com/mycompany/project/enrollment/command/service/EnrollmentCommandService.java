package com.mycompany.project.enrollment.command.service;

import com.mycompany.project.enrollment.repository.EnrollmentMapper;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EnrollmentCommandService {
    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentMapper enrollmentMapper;
}
