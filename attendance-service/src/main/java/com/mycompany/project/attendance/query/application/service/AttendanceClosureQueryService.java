package com.mycompany.project.attendance.query.application.service;

import com.mycompany.project.attendance.command.application.dto.ClosureSearchRequest;
import com.mycompany.project.attendance.query.application.dto.AttendanceClosureResponse;
import com.mycompany.project.attendance.query.repository.AttendanceClosureQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceClosureQueryService {

    private final AttendanceClosureQueryMapper attendanceClosureQueryMapper;

    /**
     * Get single closure detail
     */
    public AttendanceClosureResponse findById(Long closureId) {
        return attendanceClosureQueryMapper.findById(closureId);
    }

    /**
     * Search closure history
     */
    public List<AttendanceClosureResponse> search(ClosureSearchRequest request) {
        return attendanceClosureQueryMapper.search(request);
    }
}
