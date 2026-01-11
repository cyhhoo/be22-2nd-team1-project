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

    // 마감 이력 조회용 MyBatis Mapper
    private final AttendanceClosureQueryMapper attendanceClosureQueryMapper;

    /**
     * 마감 이력 단건(상세) 조회
     * - closure_id 기준으로 1건 조회해서 Response로 반환
     */
    public AttendanceClosureResponse findById(Long closureId) {
        return attendanceClosureQueryMapper.findById(closureId);
    }

    /**
     * 마감 이력 목록 조회(조건 검색)
     * - 학년도/범위/학년/반/강좌 등 조건으로 필터링해서 목록 반환
     */
    public List<AttendanceClosureResponse> search(ClosureSearchRequest cond) {
        return attendanceClosureQueryMapper.search(cond);
    }

    /**
     * 마감 이력 페이징용 전체 개수 조회
     * - search 조건과 동일한 조건으로 count만 조회
     */
    public long count(ClosureSearchRequest cond) {
        return attendanceClosureQueryMapper.count(cond);
    }
}
