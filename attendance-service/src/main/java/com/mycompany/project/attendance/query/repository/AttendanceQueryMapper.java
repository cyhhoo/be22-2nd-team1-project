package com.mycompany.project.attendance.query.repository;

import com.mycompany.project.attendance.command.application.dto.AttendanceSearchRequest;
import com.mycompany.project.attendance.query.application.dto.AttendanceListResponse;
import com.mycompany.project.attendance.query.application.dto.AttendanceResponse;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface AttendanceQueryMapper {

    /**
     * Get single attendance detail
     */
    AttendanceResponse findById(@Param("attendanceId") Long attendanceId);

    /**
     * Search attendance list with conditions
     */
    List<AttendanceListResponse> search(AttendanceSearchRequest cond);

    /**
     * Count attendance records for pagination
     */
    long count(AttendanceSearchRequest cond);
}
