package com.mycompany.project.enrollment.query.service;

import com.mycompany.project.enrollment.entity.EnrollmentStatus;
import com.mycompany.project.enrollment.query.dto.EnrollmentHistoryResponse;
import com.mycompany.project.enrollment.query.dto.InternalEnrollmentResponse;
import com.mycompany.project.enrollment.query.dto.TimetableResponse;
import com.mycompany.project.enrollment.repository.EnrollmentRepository;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.mycompany.project.enrollment.repository.EnrollmentMapper;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EnrollmentQueryService {

  private final EnrollmentMapper enrollmentMapper;
  private final EnrollmentRepository enrollmentRepository;

  public List<EnrollmentHistoryResponse> getMyHistory(Long userId) {
    return enrollmentMapper.selectHistoryByUserId(userId);
  }

  public List<TimetableResponse> getMyTimetable(Long userId) {
    return enrollmentMapper.selectTimetableByUserId(userId);
  }

  public List<InternalEnrollmentResponse> getInternalEnrollmentsByCourse(Long courseId, EnrollmentStatus status) {
    return enrollmentRepository.findByCourseIdAndStatus(courseId, status)
        .stream()
        .map(e -> {
          InternalEnrollmentResponse res = new InternalEnrollmentResponse();
          res.setEnrollmentId(e.getEnrollmentId());
          res.setStudentDetailId(e.getStudentDetail().getId());
          res.setCourseId(e.getCourseId());
          res.setStatus(e.getStatus());
          return res;
        })
        .toList();
  }

  public InternalEnrollmentResponse getInternalEnrollment(Long enrollmentId) {
    return enrollmentRepository.findById(enrollmentId)
        .map(e -> {
          InternalEnrollmentResponse res = new InternalEnrollmentResponse();
          res.setEnrollmentId(e.getEnrollmentId());
          res.setStudentDetailId(e.getStudentDetail().getId());
          res.setCourseId(e.getCourseId());
          res.setStatus(e.getStatus());
          return res;
        })
        .orElse(null);
  }

  public List<InternalEnrollmentResponse> searchEnrollments(
      com.mycompany.project.enrollment.query.dto.EnrollmentSearchRequest request) {
    List<com.mycompany.project.enrollment.entity.Enrollment> enrollments = enrollmentRepository.findAll();

    return enrollments.stream()
        .filter(e -> {
          if (request.getCourseIds() != null && !request.getCourseIds().isEmpty()) {
            if (!request.getCourseIds().contains(e.getCourseId())) {
              return false;
            }
          }
          if (request.getStudentIds() != null && !request.getStudentIds().isEmpty()) {
            if (!request.getStudentIds().contains(e.getStudentDetail().getUser().getUserId())) {
              return false;
            }
          }
          if (request.getStatus() != null) {
            if (!e.getStatus().name().equals(request.getStatus())) {
              return false;
            }
          }
          return true;
        })
        .map(e -> {
          InternalEnrollmentResponse res = new InternalEnrollmentResponse();
          res.setEnrollmentId(e.getEnrollmentId());
          res.setStudentDetailId(e.getStudentDetail().getId());
          res.setCourseId(e.getCourseId());
          res.setStatus(e.getStatus());
          return res;
        })
        .toList();
  }
}
