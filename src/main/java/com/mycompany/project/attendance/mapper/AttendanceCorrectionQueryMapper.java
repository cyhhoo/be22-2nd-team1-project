package com.mycompany.project.attendance.mapper;

import com.mycompany.project.attendance.dto.request.CorrectionSearchRequest;
import com.mycompany.project.attendance.dto.response.CorrectionResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceCorrectionQueryMapper {

    // 정정요청 상세 조회
    CorrectionResponse findById(@Param("requestId") Long requestId);

    // 정정요청 목록 조회 (조건 검색)
    List<CorrectionResponse> search(CorrectionSearchRequest cond);

    // 페이징용 count
    long count(CorrectionSearchRequest cond);
}
