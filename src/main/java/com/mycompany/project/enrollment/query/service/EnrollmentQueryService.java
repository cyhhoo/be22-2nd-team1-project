package com.mycompany.project.enrollment.query.service;

import com.mycompany.project.enrollment.query.dto.EnrollmentHistoryResponse;
import com.mycompany.project.enrollment.query.dto.TimetableResponse;
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

  public List<EnrollmentHistoryResponse> getMyHistory(Long userId) {
    return enrollmentMapper.selectHistoryByUserId(userId);
  }

  public List<TimetableResponse> getMyTimetable(Long userId) {
    return enrollmentMapper.selectTimetableByUserId(userId);
  }

}
