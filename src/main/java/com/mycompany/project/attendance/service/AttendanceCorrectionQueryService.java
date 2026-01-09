package com.mycompany.project.attendance.service;

import com.mycompany.project.attendance.dto.request.CorrectionSearchRequest;
import com.mycompany.project.attendance.dto.response.CorrectionResponse;
import com.mycompany.project.attendance.mapper.AttendanceCorrectionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceCorrectionQueryService {

    private final AttendanceCorrectionQueryMapper attendanceCorrectionQueryMapper;

    public CorrectionResponse findById(Long requestId) {
        return attendanceCorrectionQueryMapper.findById(requestId);
    }

    public List<CorrectionResponse> search(CorrectionSearchRequest cond) {
        return attendanceCorrectionQueryMapper.search(cond);
    }

    public long count(CorrectionSearchRequest cond) {
        return attendanceCorrectionQueryMapper.count(cond);
    }
}
