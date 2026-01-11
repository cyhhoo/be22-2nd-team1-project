package com.mycompany.project.attendance.mapper;


import com.mycompany.project.attendance.dto.request.ClosureSearchRequest;
import com.mycompany.project.attendance.dto.response.AttendanceClosureResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceClosureQueryMapper {

    // 마감이력 상세 조회
    AttendanceClosureResponse findById(@Param("closureId") Long closureId);

    // 마감이력 목록 조회 (조건 검색)
    List<AttendanceClosureResponse> search(ClosureSearchRequest cond);

    // 페이징용 count
    long count(ClosureSearchRequest cond);


}
