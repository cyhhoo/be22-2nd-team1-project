package com.mycompany.project.attendance.service;

import com.mycompany.project.attendance.dto.response.AttendanceCodeResponse;
import com.mycompany.project.attendance.mapper.AttendanceCodeQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceCodeQueryService {

    private final AttendanceCodeQueryMapper attendanceCodeQueryMapper;

    public AttendanceCodeResponse findById(Long attendanceCodeId) {
        return attendanceCodeQueryMapper.findById(attendanceCodeId);
    }

    public List<AttendanceCodeResponse> findAll(Boolean activeOnly) {
        return attendanceCodeQueryMapper.findAll(activeOnly);
    }
}
