package com.mycompany.project.attendance.service;

import com.mycompany.project.attendance.dto.response.AttendanceCodeResponse;
import com.mycompany.project.attendance.mapper.AttendanceCodeQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceCodeQueryService {

    // 출결 코드 조회용 MyBatis Mapper
    private final AttendanceCodeQueryMapper attendanceCodeQueryMapper;

    /**
     * 출결 코드 단건 조회
     * - attendance_code_id로 1건 조회해서 반환
     */
    public AttendanceCodeResponse findById(Long attendanceCodeId) {
        return attendanceCodeQueryMapper.findById(attendanceCodeId);
    }

    /**
     * 출결 코드 목록 조회
     * - activeOnly=true면 사용중(is_active=1) 코드만 가져오도록 Mapper XML에서 조건 처리
     * - activeOnly=null/false면 전체 코드 조회
     */
    public List<AttendanceCodeResponse> findAll(Boolean activeOnly) {
        return attendanceCodeQueryMapper.findAll(activeOnly);
    }
}