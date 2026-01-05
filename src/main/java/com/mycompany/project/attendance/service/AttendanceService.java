package com.mycompany.project.attendance.service;

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import com.mycompany.project.attendance.repository.AttendanceRepository;
import com.mycompany.project.attendance.mapper.AttendanceMapper;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
}
