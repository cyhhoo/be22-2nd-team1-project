package com.mycompany.project.attendance.mapper;

import com.mycompany.project.attendance.dto.request.AttendanceSearchRequest;
import com.mycompany.project.attendance.dto.response.AttendanceClosureResponse;
import com.mycompany.project.attendance.dto.response.AttendanceListResponse;
import com.mycompany.project.attendance.dto.response.AttendanceResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceQueryMapper {

    // 출결 상세 조회 (PK 단건 조회이다)
    AttendanceResponse findById(@Param("attendanceId") Long attendanceId);

    // 출결 목록 조회 (조건 검색)
    List<AttendanceListResponse> search(AttendanceSearchRequest cond);

    // 페이징 쓸 때 total count 뽑는 용도
    long count(AttendanceSearchRequest cond);

}
