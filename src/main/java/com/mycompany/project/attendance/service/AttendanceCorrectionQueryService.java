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

    // 정정요청 조회용 MyBatis Mapper
    private final AttendanceCorrectionQueryMapper attendanceCorrectionQueryMapper;

    /**
     * 정정요청 단건(상세) 조회
     * - request_id 기준으로 1건 조회해서 Response로 반환
     */
    public CorrectionResponse findById(Long requestId) {
        return attendanceCorrectionQueryMapper.findById(requestId);
    }

    /**
     * 정정요청 목록 조회(조건 검색)
     * - 상태(PENDING/APPROVED/REJECTED), 날짜, 요청자, 출결ID 등 조건으로 필터링해서 목록 반환
     */
    public List<CorrectionResponse> search(CorrectionSearchRequest cond) {
        return attendanceCorrectionQueryMapper.search(cond);
    }

    /**
     * 정정요청 페이징용 전체 개수 조회
     * - search 조건과 동일한 조건으로 count만 조회
     */
    public long count(CorrectionSearchRequest cond) {
        return attendanceCorrectionQueryMapper.count(cond);
    }
}