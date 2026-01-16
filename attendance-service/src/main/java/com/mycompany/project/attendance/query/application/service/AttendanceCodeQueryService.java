package com.mycompany.project.attendance.query.application.service;

import com.mycompany.project.attendance.query.application.dto.AttendanceCodeResponse;
import com.mycompany.project.attendance.query.repository.AttendanceCodeQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceCodeQueryService {

    private final AttendanceCodeQueryMapper attendanceCodeQueryMapper;

    /**
     * Get single attendance code detail
     */
    public AttendanceCodeResponse findById(Long id) {
        return attendanceCodeQueryMapper.findById(id);
    }

    /**
     * Get all attendance codes
     */
    public List<AttendanceCodeResponse> findAll(Boolean activeOnly) {
        return attendanceCodeQueryMapper.findAll(activeOnly != null && activeOnly);
    }
}
