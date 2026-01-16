package com.mycompany.project.attendance.query.application.service;

import com.mycompany.project.attendance.command.application.dto.CorrectionSearchRequest;
import com.mycompany.project.attendance.query.application.dto.CorrectionResponse;
import com.mycompany.project.attendance.query.repository.AttendanceCorrectionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceCorrectionQueryService {

    private final AttendanceCorrectionQueryMapper attendanceCorrectionQueryMapper;

    /**
     * Get single correction request detail
     */
    public CorrectionResponse findById(Long requestId) {
        return attendanceCorrectionQueryMapper.findById(requestId);
    }

    /**
     * Search correction requests
     */
    public List<CorrectionResponse> search(CorrectionSearchRequest request) {
        return attendanceCorrectionQueryMapper.search(request);
    }
}
