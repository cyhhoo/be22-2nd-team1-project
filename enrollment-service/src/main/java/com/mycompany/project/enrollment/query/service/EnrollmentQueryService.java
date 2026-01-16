package com.mycompany.project.enrollment.query.service;

import com.mycompany.project.enrollment.command.domain.aggregate.Enrollment;
import com.mycompany.project.common.enums.EnrollmentStatus;
import com.mycompany.project.enrollment.query.dto.EnrollmentHistoryResponse;
import com.mycompany.project.enrollment.query.dto.InternalEnrollmentResponse;
import com.mycompany.project.enrollment.query.dto.TimetableResponse;
import com.mycompany.project.enrollment.command.domain.repository.EnrollmentRepository;
import com.mycompany.project.enrollment.command.domain.repository.EnrollmentMapper;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EnrollmentQueryService {

  private final EnrollmentMapper enrollmentMapper;
  private final EnrollmentRepository enrollmentRepository;

  public List<EnrollmentHistoryResponse> getMyHistory(Long studentDetailId) {
    return enrollmentMapper.selectHistoryByUserId(studentDetailId);
  }

  public List<TimetableResponse> getMyTimetable(Long studentDetailId) {
    return enrollmentMapper.selectTimetableByUserId(studentDetailId);
  }

  public List<InternalEnrollmentResponse> getInternalEnrollmentsByCourse(Long courseId, EnrollmentStatus status) {
    return enrollmentRepository.findByCourseIdAndStatus(courseId, status)
        .stream()
        .map(this::convertToInternalResponse)
        .collect(Collectors.toList());
  }

  public InternalEnrollmentResponse getInternalEnrollment(Long enrollmentId) {
    return enrollmentRepository.findById(java.util.Objects.requireNonNull(enrollmentId))
        .map(this::convertToInternalResponse)
        .orElse(null);
  }

  public List<InternalEnrollmentResponse> searchEnrollments(
      com.mycompany.project.enrollment.query.dto.EnrollmentSearchRequest request) {
    // In MSA, complex searching across services often uses Query services or ID
    // collections.
    // For now, we use a simple filter on existing repository data.
    List<Enrollment> enrollments = enrollmentRepository.findAll();

    return enrollments.stream()
        .filter(e -> {
          if (request.getCourseIds() != null && !request.getCourseIds().isEmpty()) {
            if (!request.getCourseIds().contains(e.getCourseId()))
              return false;
          }
          if (request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
            // Note: In refined MSA, studentIds here would be studentDetailIds.
            if (!request.getStudentIds().contains(e.getStudentDetailId()))
              return false;
          }
          if (request.getStatus() != null) {
            if (!e.getStatus().name().equals(request.getStatus()))
              return false;
          }
          return true;
        })
        .map(this::convertToInternalResponse)
        .collect(Collectors.toList());
  }

  private InternalEnrollmentResponse convertToInternalResponse(Enrollment e) {
    InternalEnrollmentResponse res = new InternalEnrollmentResponse();
    res.setEnrollmentId(e.getEnrollmentId());
    res.setStudentDetailId(e.getStudentDetailId());
    res.setCourseId(e.getCourseId());
    res.setStatus(e.getStatus());
    return res;
  }
}
