package com.mycompany.project.attendance.service;

import com.mycompany.project.attendance.dto.request.ClosureSearchRequest;
import com.mycompany.project.attendance.dto.response.AttendanceClosureResponse;
import com.mycompany.project.attendance.mapper.AttendanceClosureQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceClosureQueryService {

    private final AttendanceClosureQueryMapper attendanceClosureQueryMapper;

    public AttendanceClosureResponse findById(Long closureId) {
        return attendanceClosureQueryMapper.findById(closureId);
    }

    public List<AttendanceClosureResponse> search(ClosureSearchRequest cond) {
        return attendanceClosureQueryMapper.search(cond);
    }

    public long count(ClosureSearchRequest cond) {
        return attendanceClosureQueryMapper.count(cond);
    }
}
