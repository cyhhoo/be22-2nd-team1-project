package com.mycompany.project.attendance.mapper;

import com.mycompany.project.attendance.dto.response.AttendanceCodeResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceCodeQueryMapper {

    // 출결 코드 단건 조회
    AttendanceCodeResponse findById(@Param("attendanceCodeId") Long attendanceCodeId);

    // 출결 코드 전체 조회
    // - activeOnly=true면 is_active=1만 가져오도록 XML에서 조건 처리
    List<AttendanceCodeResponse> findAll(@Param("activeOnly") Boolean activeOnly);

}
