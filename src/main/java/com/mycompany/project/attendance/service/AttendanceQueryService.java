package com.mycompany.project.attendance.service;

import com.mycompany.project.attendance.dto.request.AttendanceSearchRequest;
import com.mycompany.project.attendance.dto.response.AttendanceListResponse;
import com.mycompany.project.attendance.dto.response.AttendanceResponse;
import com.mycompany.project.attendance.mapper.AttendanceQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceQueryService {

    private final AttendanceQueryMapper attendanceQueryMapper;

    public AttendanceResponse findById(Long attendanceId) {
        return attendanceQueryMapper.findById(attendanceId);
    }

    public List<AttendanceListResponse> search(AttendanceSearchRequest cond) {
        return attendanceQueryMapper.search(cond);
    }

    public long count(AttendanceSearchRequest cond) {
        return attendanceQueryMapper.count(cond);
    }
}
